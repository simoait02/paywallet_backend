package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;
import com.paylogic.paywalletlite.dto.request.TokenAllocationRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.exception.DoubleSpendException;
import com.paylogic.paywalletlite.exception.InsufficientFundsException;
import com.paylogic.paywalletlite.exception.TokenExpiredException;
import com.paylogic.paywalletlite.repository.token.TokenRepository;
import com.paylogic.paywalletlite.repository.token.TokenTransferNodeRepository;
import com.paylogic.paywalletlite.repository.wallet.WalletRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
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
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final TokenTransferNodeRepository transferNodeRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TokenGenerationService tokenGenerationService;
    private final TokenSignatureService tokenSignatureService;
    private final TokenValidationService tokenValidationService;
    private final SignatureVerificationService signatureVerificationService;

    @Autowired
    public TokenServiceImpl(TokenRepository tokenRepository,
                            TokenTransferNodeRepository transferNodeRepository,
                            WalletRepository walletRepository,
                            WalletService walletService,
                            TokenGenerationService tokenGenerationService,
                            TokenSignatureService tokenSignatureService,
                            TokenValidationService tokenValidationService,
                            SignatureVerificationService signatureVerificationService) {
        this.tokenRepository = tokenRepository;
        this.transferNodeRepository = transferNodeRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
        this.tokenGenerationService = tokenGenerationService;
        this.tokenSignatureService = tokenSignatureService;
        this.tokenValidationService = tokenValidationService;
        this.signatureVerificationService = signatureVerificationService;
    }

    // ============================================================
    // ALLOCATION
    // ============================================================

    @Override
    @Transactional
    public List<Token> allocateTokens(UUID walletId, TokenAllocationRequestDto request) throws BusinessException {
        Wallet wallet = walletService.findById(walletId);
        System.out.println("WALLET FOUND !");
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException("Wallet is not active");
        }

        BigDecimal totalAmount = request.getAmount();
        if (wallet.getOnlineBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientFundsException("Insufficient online balance for token allocation");
        }
        System.out.println("BALANCE VERIFIED !");
        // Étape 1 : Générer les tokens via la stratégie
        List<Token> generatedTokens = tokenGenerationService.generate(wallet, totalAmount);

        System.out.println("TOKENS GENERATED !");

        // Étape 2 : Signer chaque token avec la clé serveur
        for (Token token : generatedTokens) {
            token.setStatus(TokenStatus.ALLOCATED);
            tokenRepository.save(token);
            tokenSignatureService.signToken(token);
        }

        // Étape 3 : Mettre à jour les soldes du wallet
        wallet.setOnlineBalance(wallet.getOnlineBalance().subtract(totalAmount));
        wallet.setOfflineBalance(wallet.getOfflineBalance().add(totalAmount));
        walletRepository.save(wallet);

        return generatedTokens;
    }

    @Override
    @Transactional
    public List<Token> allocateTokensForOffline(UUID walletId, BigDecimal amount) throws BusinessException {
        TokenAllocationRequestDto request = new TokenAllocationRequestDto();
        request.setAmount(amount);
        request.setLifetimeHours(72);
        request.setMaxTransfers(3);
        return allocateTokens(walletId, request);
    }

    // ============================================================
    // QUERY
    // ============================================================

    @Override
    public Token findById(UUID tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));
    }

    @Override
    public List<Token> findByWalletId(UUID walletId) {
        return tokenRepository.findByHolderWalletId(walletId);
    }

    @Override
    public List<Token> findByWalletIdAndStatus(UUID walletId, TokenStatus status) {
        return tokenRepository.findByHolderWalletIdAndStatus(walletId, status);
    }

    @Override
    public Token findByNonce(String nonce) {
        return tokenRepository.findByNonce(nonce)
                .orElseThrow(() -> new BusinessException("Token not found with nonce: " + nonce));
    }

    // ============================================================
    // TRANSFER (côté server — enregistrement du transfert)
    // ============================================================

    @Override
    @Transactional
    public TokenTransferNode transferToken(UUID tokenId, UUID fromWalletId, UUID toWalletId,
                                           String payerSignature, LocalDateTime transferTimestamp)
            throws BusinessException {

        Token token = findById(tokenId);

        // Validation métier via TokenValidationService
        tokenValidationService.validateForTransfer(token);
        tokenValidationService.validateOwnership(token, fromWalletId);

        if (token.getTransferCount() >= token.getMaxTransfers()) {
            throw new BusinessException("Maximum transfer count reached");
        }

        // Vérifier la signature du payer via SignatureVerificationService
        Wallet fromWallet = walletService.findById(fromWalletId);
        String transferPayload = buildTransferPayload(token, fromWalletId, toWalletId);
        boolean validSignature = signatureVerificationService.verifyOfflineTransfer(
                transferPayload, payerSignature, fromWallet.getPublicKey()
        );
        if (!validSignature) {
            throw new BusinessException("Invalid payer signature for transfer");
        }

        Wallet toWallet = walletService.findById(toWalletId);
        if (toWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException("Destination wallet is not active");
        }

        // Créer le nœud de transfert
        TokenTransferNode node = new TokenTransferNode();
        node.setToken(token);
        node.setPayerWallet(fromWallet);
        node.setPayeeWallet(toWallet);
        node.setTransferredAmount(token.getValue());
        node.setTransferTimestamp(transferTimestamp);
        node.setPayerSignature(payerSignature);
        node.setTransferHash(generateTransferHash(token, fromWalletId, toWalletId));

        // Mettre à jour le token
        token.setCurrentHolderWallet(toWallet);
        token.setStatus(TokenStatus.TRANSFERRED);
        token.setTransferCount(token.getTransferCount() + 1);
        token.setLastTransferAt(transferTimestamp);

        tokenRepository.save(token);
        transferNodeRepository.save(node);

        return node;
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    @Override
    public boolean validateTokenForOfflinePayment(UUID tokenId, UUID walletId) throws BusinessException {
        Token token = findById(tokenId);

        if (token.getStatus() != TokenStatus.ALLOCATED && token.getStatus() != TokenStatus.TRANSFERRED) {
            return false;
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (!token.getCurrentHolderWallet().getWalletId().equals(walletId)) {
            return false;
        }

        // Vérifier l'intégrité cryptographique
        return tokenSignatureService.verifyTokenIntegrity(token);
    }

    @Override
    public boolean verifyTokenSignature(UUID tokenId, String signature) throws BusinessException {
        Token token = findById(tokenId);
        return tokenSignatureService.verifyTokenSignature(token);
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    @Transactional
    public void expireToken(UUID tokenId) {
        tokenRepository.updateStatus(tokenId, TokenStatus.EXPIRED);
    }

    @Override
    @Transactional
    public void revokeToken(UUID tokenId, String reason) {
        Token token = findById(tokenId);
        token.setStatus(TokenStatus.REVOKED);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void cleanupExpiredTokens() {
        List<Token> expired = tokenRepository.findExpiredTokens();
        for (Token token : expired) {
            token.setStatus(TokenStatus.EXPIRED);
            tokenRepository.save(token);
        }
    }

    // ============================================================
    // STATISTICS
    // ============================================================

    @Override
    public long countActiveTokensByWallet(UUID walletId) {
        return tokenRepository.countByHolderWalletIdAndStatus(walletId, TokenStatus.ALLOCATED)
                + tokenRepository.countByHolderWalletIdAndStatus(walletId, TokenStatus.TRANSFERRED);
    }

    @Override
    public BigDecimal getTotalValueByWallet(UUID walletId) {
        List<Token> tokens = new ArrayList<Token>();
        tokens.addAll(tokenRepository.findByHolderWalletIdAndStatus(walletId, TokenStatus.ALLOCATED));
        tokens.addAll(tokenRepository.findByHolderWalletIdAndStatus(walletId, TokenStatus.TRANSFERRED));

        BigDecimal total = BigDecimal.ZERO;
        for (Token token : tokens) {
            total = total.add(token.getValue());
        }
        return total;
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private String buildTransferPayload(Token token, UUID fromWalletId, UUID toWalletId) {
        return String.join("|",
                token.getTokenId().toString(),
                token.getTokenHash(),
                fromWalletId.toString(),
                toWalletId.toString(),
                token.getValue().toPlainString(),
                LocalDateTime.now().toString()
        );
    }

    private String generateTransferHash(Token token, UUID fromWalletId, UUID toWalletId) {
        String data = String.join("|",
                token.getTokenId().toString(),
                fromWalletId.toString(),
                toWalletId.toString(),
                LocalDateTime.now().toString()
        );
        return HashUtil.sha256(data);
    }
}