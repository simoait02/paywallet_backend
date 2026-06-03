package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.token.TokenRepository;
import com.paylogic.paywalletlite.repository.token.TokenTransferNodeRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import com.paylogic.paywalletlite.service.security.SignatureVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TokenTransferTraceServiceImpl implements TokenTransferTraceService {

    private final TokenTransferNodeRepository transferNodeRepository;
    private final TokenRepository tokenRepository;
    private final TokenValidationService validationService;
    private final SignatureVerificationService signatureVerificationService;


    @Autowired
    public TokenTransferTraceServiceImpl(TokenTransferNodeRepository transferNodeRepository,
                                         TokenRepository tokenRepository,
                                         TokenValidationService validationService,
                                         SignatureVerificationService signatureVerificationService) {
        this.transferNodeRepository = transferNodeRepository;
        this.tokenRepository = tokenRepository;
        this.validationService = validationService;
        this.signatureVerificationService = signatureVerificationService;

    }

    // ============================================================
    // ENREGISTREMENT DES NŒUDS
    // ============================================================

    @Override
    @Transactional
    public TokenTransferNode recordIssuance(Token token, Wallet issuerWallet) throws BusinessException {
        validationService.validateExists(token);

        if (issuerWallet == null) {
            throw new BusinessException("Issuer wallet cannot be null");
        }

        TokenTransferNode node = new TokenTransferNode();
        node.setToken(token);
        node.setPayerWallet(issuerWallet); // Le backend est le "payer" initial
        node.setPayeeWallet(issuerWallet); // ...et le "payee" initial (auto-émission)
        node.setTransferredAmount(token.getValue());
        node.setTransferTimestamp(LocalDateTime.now());
        node.setPayerSignature("SERVER_ISSUANCE_SIGNATURE"); // Signé par le serveur
        node.setPayerCertificate(issuerWallet.getCertificateId());
        node.setTransferHash(generateTransferHash(token, issuerWallet.getWalletId(), issuerWallet.getWalletId(), "ISSUANCE"));

        return transferNodeRepository.save(node);
    }

    @Override
    @Transactional
    public TokenTransferNode recordTransfer(Token token, Wallet fromWallet, Wallet toWallet, String payerSignature) throws BusinessException {
        validationService.validateExists(token);
        validationService.validateForTransfer(token);

        if (fromWallet == null || toWallet == null) {
            throw new BusinessException("Both payer and payee wallets are required");
        }
        if (payerSignature == null || payerSignature.isEmpty()) {
            throw new BusinessException("Payer signature is required for transfer trace");
        }

        // Vérifier que le payer est bien le détenteur actuel
        Wallet currentHolder = token.getCurrentHolderWallet();
        if (currentHolder == null || !currentHolder.getWalletId().equals(fromWallet.getWalletId())) {
            throw new BusinessException("Payer is not the current token holder");
        }

        TokenTransferNode node = new TokenTransferNode();
        node.setToken(token);
        node.setPayerWallet(fromWallet);
        node.setPayeeWallet(toWallet);
        node.setTransferredAmount(token.getValue());
        node.setTransferTimestamp(LocalDateTime.now());
        node.setPayerSignature(payerSignature);
        node.setPayerCertificate(fromWallet.getCertificateId());
        node.setTransferHash(generateTransferHash(token, fromWallet.getWalletId(), toWallet.getWalletId(), payerSignature));

        return transferNodeRepository.save(node);
    }

    @Override
    @Transactional
    public TokenTransferNode recordRedemption(Token token, Wallet redeemerWallet, String validationSignature) throws BusinessException {
        validationService.validateExists(token);

        if (redeemerWallet == null) {
            throw new BusinessException("Redeemer wallet cannot be null");
        }

        TokenTransferNode node = new TokenTransferNode();
        node.setTransferNodeId(UUID.randomUUID());
        node.setToken(token);
        node.setPayerWallet(redeemerWallet); // Le redeemer "rend" le token au système
        node.setPayeeWallet(redeemerWallet); // ...ou à un wallet système
        node.setTransferredAmount(token.getValue());
        node.setTransferTimestamp(LocalDateTime.now());
        node.setPayerSignature(validationSignature != null ? validationSignature : "REDEMPTION_SIGNATURE");
        node.setPayerCertificate(redeemerWallet.getCertificateId());
        node.setTransferHash(generateTransferHash(token, redeemerWallet.getWalletId(), redeemerWallet.getWalletId(), "REDEMPTION"));

        return transferNodeRepository.save(node);
    }

    // ============================================================
    // RECONSTRUCTION ET VÉRIFICATION DE LA CHAÎNE
    // ============================================================

    @Override
    public List<TokenTransferNode> reconstructChain(UUID tokenId) throws BusinessException {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));

        List<TokenTransferNode> chain = transferNodeRepository.findByTokenIdOrdered(tokenId);

        if (chain.isEmpty()) {
            throw new BusinessException("No transfer history found for token: " + tokenId);
        }

        return chain;
    }

    @Override
    public boolean verifyChainIntegrity(UUID tokenId) throws BusinessException {
        List<TokenTransferNode> chain = reconstructChain(tokenId);

        if (chain.isEmpty()) {
            return false;
        }

        for (int i = 0; i < chain.size(); i++) {
            TokenTransferNode node = chain.get(i);

            // 1. Vérifier le hash du transfert
            String expectedHash = generateTransferHash(
                    node.getToken(),
                    node.getPayerWalletId(),
                    node.getPayeeWalletId(),
                    node.getPayerSignature()
            );
            if (!expectedHash.equals(node.getTransferHash())) {
                return false; // Hash corrompu
            }

            // 2. Vérifier la signature de chaque transfert (sauf le nœud d'émission)
            if (i > 0 && node.getPayerSignature() != null
                    && !node.getPayerSignature().equals("SERVER_ISSUANCE_SIGNATURE")) {
                Wallet payerWallet = node.getPayerWallet();
                String payload = buildVerificationPayload(node);
                boolean signatureValid = signatureVerificationService.verifyOfflineTransfer(
                        payload,
                        node.getPayerSignature(),
                        payerWallet.getPublicKey()
                );
                if (!signatureValid) {
                    return false; // Signature invalide
                }
            }

            // 3. Vérifier la continuité
            if (i < chain.size() - 1) {
                TokenTransferNode nextNode = chain.get(i + 1);
                if (!node.getPayeeWalletId().equals(nextNode.getPayerWalletId())) {
                    return false; // Chaîne brisée
                }
            }
        }

        return true;
    }

    private String buildVerificationPayload(TokenTransferNode node) {
        return String.join("|",
                node.getToken().getTokenId().toString(),
                node.getToken().getTokenHash(),
                node.getPayerWalletId().toString(),
                node.getPayeeWalletId().toString(),
                node.getTransferredAmount().toPlainString(),
                node.getTransferTimestamp().toString()
        );
    }

    @Override
    public Wallet getCurrentHolder(UUID tokenId) throws BusinessException {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));

        // Le détenteur actuel est dans le token (currentHolderWallet)
        // ou le dernier payee de la chaîne
        if (token.getCurrentHolderWallet() != null) {
            return token.getCurrentHolderWallet();
        }

        Optional<TokenTransferNode> latest = transferNodeRepository.findLatestByTokenId(tokenId);
        return latest.map(TokenTransferNode::getPayeeWallet)
                .orElseThrow(() -> new BusinessException("Cannot determine current holder for token: " + tokenId));
    }

    @Override
    public Wallet getOriginalIssuer(UUID tokenId) throws BusinessException {
        Optional<TokenTransferNode> first = transferNodeRepository.findFirstByTokenId(tokenId);
        return first.map(TokenTransferNode::getPayerWallet)
                .orElseThrow(() -> new BusinessException("Cannot determine original issuer for token: " + tokenId));
    }

    @Override
    public long getTransferCount(UUID tokenId) {
        return transferNodeRepository.countByTokenId(tokenId);
    }

    @Override
    public boolean hasWalletHeldToken(UUID tokenId, UUID walletId) {
        List<TokenTransferNode> history = transferNodeRepository.findByTokenId(tokenId);

        return history.stream().anyMatch(node ->
                node.getPayerWalletId().equals(walletId) || node.getPayeeWalletId().equals(walletId)
        );
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private String generateTransferHash(Token token, UUID fromWalletId, UUID toWalletId, String signature) {
        String data = String.join("|",
                token.getTokenId().toString(),
                fromWalletId.toString(),
                toWalletId.toString(),
                token.getValue().toPlainString(),
                LocalDateTime.now().toString(),
                signature
        );
        return HashUtil.sha256(data);
    }
}