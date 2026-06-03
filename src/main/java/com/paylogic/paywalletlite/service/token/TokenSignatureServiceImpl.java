package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenSignature;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.token.TokenSignatureRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import com.paylogic.paywalletlite.service.security.CryptographicService;
import com.paylogic.paywalletlite.service.security.SignatureVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service responsable de la signature cryptographique des tokens.
 * Utilise CryptographicService pour la gestion des clés serveur.
 */
@Service
@Transactional(readOnly = true)
public class TokenSignatureServiceImpl implements TokenSignatureService {

    private static final String SIGNATURE_ALGORITHM = "ECDSA_SHA256";

    private final TokenSignatureRepository signatureRepository;
    private final CryptographicService cryptographicService;
    private final SignatureVerificationService signatureVerificationService;

    @Autowired
    public TokenSignatureServiceImpl(TokenSignatureRepository signatureRepository,
                                     CryptographicService cryptographicService,
                                     SignatureVerificationService signatureVerificationService) {
        this.signatureRepository = signatureRepository;
        this.cryptographicService = cryptographicService;
        this.signatureVerificationService = signatureVerificationService;
    }

    // ============================================================
    // SIGNATURE
    // ============================================================

    @Override
    @Transactional
    public TokenSignature signToken(Token token) throws BusinessException {
        if (token == null) {
            throw new BusinessException("Cannot sign null token");
        }

        // Récupérer la clé active de signature de tokens
        ServerKey signingKey = cryptographicService.getActiveKey(ServerKeyPurpose.TOKEN_SIGNING);

        // Construire les données à signer
        String signedData = buildSignedData(token);
        String signedDataHash = HashUtil.sha256(signedData);

        // Signer avec la clé privée du serveur (via CryptographicService)
        String signatureValue = cryptographicService.signData(signedDataHash, signingKey.getServerKeyId());

        // Créer et persister la signature
        TokenSignature tokenSignature = new TokenSignature();
        tokenSignature.setToken(token);
        tokenSignature.setSignatureAlgorithm(SIGNATURE_ALGORITHM);
        tokenSignature.setSignatureValue(signatureValue);
        tokenSignature.setSignedDataHash(signedDataHash);
        tokenSignature.setSignedAt(LocalDateTime.now());
        tokenSignature.setIssuerPublicKey(signingKey.getPublicKeyPem());

        TokenSignature saved = signatureRepository.save(tokenSignature);
        token.setTokenSignature(saved);

        return saved;
    }

    // ============================================================
    // VÉRIFICATION
    // ============================================================

    @Override
    public boolean verifyTokenSignature(Token token) throws BusinessException {
        if (token == null || token.getTokenSignature() == null) {
            return false;
        }

        // Déléguer à SignatureVerificationService
        return signatureVerificationService.verifyTokenSignature(token);
    }

    @Override
    public boolean verifyTokenSignature(OfflineTransactionToken token) throws BusinessException {
        if (token == null || token.getBackendSignature() == null) {
            return false;
        }

        TokenSignature tSgn = findByTokenId(token.getTokenId());

        System.out.println("######  Token Hash DB : "+ tSgn.getSignedDataHash());
        // Déléguer à SignatureVerificationService
        return signatureVerificationService.verifyTokenSignature(token);
    }

    @Override
    public boolean verifyTokenIntegrity(Token token) {
        if (token == null || token.getTokenHash() == null) {
            return false;
        }

        // Vérifier que le tokenHash correspond aux données
        String expectedHash = buildTokenHash(token);
        if (!expectedHash.equals(token.getTokenHash())) {
            return false;
        }

        // Vérifier la signature cryptographique si présente
        if (token.getTokenSignature() != null) {
            try {
                return verifyTokenSignature(token);
            } catch (BusinessException e) {
                return false;
            }
        }

        return true;
    }

    // ============================================================
    // RECHERCHE / SUPPRESSION
    // ============================================================

    @Override
    public TokenSignature findByTokenId(UUID tokenId) {
        return signatureRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new BusinessException("Signature not found for token: " + tokenId));
    }

    @Override
    @Transactional
    public void deleteSignature(UUID tokenId) {
        signatureRepository.deleteByTokenId(tokenId);
    }

    // ============================================================
    // CONSTRUCTION DES DONNÉES
    // ============================================================

    /**
     * Données signées : tokenHash | issuerId | value | issuedAt | nonce
     */
    private String buildSignedData(Token token) {
        return String.join("|",
                token.getTokenHash(),
                token.getIssuerId().toString(),
                token.getValue().toPlainString(),
                token.getIssuedAt().toString(),
                token.getNonce()
        );
    }

    /**
     * Hash du token : tokenId | nonce | value | issuerWalletId | issuedAt
     */
    private String buildTokenHash(Token token) {
        return HashUtil.sha256(String.join("|",
                token.getTokenId().toString(),
                token.getNonce(),
                token.getValue().toPlainString(),
                token.getIssuerWalletId().toString(),
                token.getIssuedAt().toString()
        ));
    }
}