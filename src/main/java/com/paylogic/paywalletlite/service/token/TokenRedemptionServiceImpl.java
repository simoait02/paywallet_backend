package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.exception.DoubleSpendException;
import com.paylogic.paywalletlite.repository.token.TokenRepository;
import com.paylogic.paywalletlite.repository.wallet.WalletRepository;
import com.paylogic.paywalletlite.service.security.SignatureVerificationService;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TokenRedemptionServiceImpl implements TokenRedemptionService {

    private final TokenRepository tokenRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TokenLifecycleService lifecycleService;
    private final TokenValidationService validationService;
    private final TokenSignatureService signatureService;
    private final SignatureVerificationService signatureVerificationService;

    @Autowired
    public TokenRedemptionServiceImpl(TokenRepository tokenRepository,
                                      WalletRepository walletRepository,
                                      WalletService walletService,
                                      TokenLifecycleService lifecycleService,
                                      TokenValidationService validationService,
                                      TokenSignatureService signatureService,
                                      SignatureVerificationService signatureVerificationService) {
        this.tokenRepository = tokenRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
        this.lifecycleService = lifecycleService;
        this.validationService = validationService;
        this.signatureService = signatureService;
        this.signatureVerificationService = signatureVerificationService;
    }

    @Override
    @Transactional
    public Token redeemToken(UUID tokenId, UUID redeemerWalletId, String validationSignature)
            throws BusinessException {

        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));

        Wallet redeemerWallet = walletService.findById(redeemerWalletId);

        if (redeemerWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException("Redeemer wallet is not active: " + redeemerWalletId);
        }

        // Validations
        validationService.validateForRedemption(token);
        validationService.validateOwnership(token, redeemerWalletId);
        validationService.validateSignatureIntegrity(token);

        // Vérification cryptographique
        if (!signatureService.verifyTokenSignature(token)) {
            throw new BusinessException("Token signature verification failed: " + tokenId);
        }

        // Vérifier validation signature si fournie
        if (validationSignature != null && !validationSignature.isEmpty()) {
            String redemptionPayload = buildRedemptionPayload(token, redeemerWalletId);
            boolean validRedeemerSig = signatureVerificationService.verifyOfflineTransfer(
                    redemptionPayload, validationSignature, redeemerWallet.getPublicKey()
            );
            if (!validRedeemerSig) {
                throw new BusinessException("Invalid redemption signature for token: " + tokenId);
            }
        }

        // Anti double-spending
        if (token.getStatus() == TokenStatus.REDEEMED) {
            throw new DoubleSpendException("Token already redeemed: " + tokenId);
        }

        BigDecimal tokenValue = token.getValue();

        // CORRECTION : Mettre à jour les soldes correctement
        // Le token reçu en offline est dans pending_balance, pas dans offline_balance
        if (token.getStatus() == TokenStatus.TRANSFERRED) {
            // Token reçu via transfert offline → décrémenter pending_balance
            redeemerWallet.setPendingBalance(
                    redeemerWallet.getPendingBalance().subtract(tokenValue)
            );
            // S'assurer que pending_balance ne devient pas négatif
            if (redeemerWallet.getPendingBalance().compareTo(BigDecimal.ZERO) < 0) {
                redeemerWallet.setPendingBalance(BigDecimal.ZERO);
            }
        } else {
            // Token ALLOCATED → décrémenter offline_balance
            redeemerWallet.setOfflineBalance(
                    redeemerWallet.getOfflineBalance().subtract(tokenValue)
            );
            if (redeemerWallet.getOfflineBalance().compareTo(BigDecimal.ZERO) < 0) {
                redeemerWallet.setOfflineBalance(BigDecimal.ZERO);
            }
        }

        // Ajouter au solde online dans tous les cas
        redeemerWallet.setOnlineBalance(redeemerWallet.getOnlineBalance().add(tokenValue));

        // Finaliser
        token.setStatus(TokenStatus.REDEEMED);
        tokenRepository.save(token);
        walletRepository.save(redeemerWallet);

        return token;
    }

    @Override
    @Transactional
    public List<Token> redeemTokens(List<UUID> tokenIds, UUID redeemerWalletId) throws BusinessException {
        List<Token> redeemedTokens = new ArrayList<Token>();
        BigDecimal totalValue = BigDecimal.ZERO;

        // Validation préliminaire de tous les tokens
        for (UUID tokenId : tokenIds) {
            Token token = tokenRepository.findById(tokenId)
                    .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));

            validationService.validateForRedemption(token);
            validationService.validateOwnership(token, redeemerWalletId);

            totalValue = totalValue.add(token.getValue());
        }

        // Redeem chaque token
        for (UUID tokenId : tokenIds) {
            Token redeemed = redeemToken(tokenId, redeemerWalletId, null);
            redeemedTokens.add(redeemed);
        }

        return redeemedTokens;
    }

    @Override
    public BigDecimal calculateRedemptionValue(List<UUID> tokenIds) throws BusinessException {
        BigDecimal total = BigDecimal.ZERO;

        for (UUID tokenId : tokenIds) {
            Token token = tokenRepository.findById(tokenId)
                    .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));

            if (token.getStatus() == TokenStatus.REDEEMED || token.getStatus() == TokenStatus.INVALID) {
                continue;
            }

            total = total.add(token.getValue());
        }

        return total;
    }

    @Override
    public boolean isEligibleForRedemption(UUID tokenId, UUID walletId) {
        try {
            Token token = tokenRepository.findById(tokenId)
                    .orElseThrow(() -> new BusinessException("Token not found"));

            validationService.validateForRedemption(token);
            validationService.validateOwnership(token, walletId);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private String buildRedemptionPayload(Token token, UUID redeemerWalletId) {
        return String.join("|",
                token.getTokenId().toString(),
                token.getTokenHash(),
                redeemerWalletId.toString(),
                token.getValue().toPlainString(),
                LocalDateTime.now().toString(),
                "REDEMPTION"
        );
    }
}