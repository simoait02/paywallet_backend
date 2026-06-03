package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenSignature;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.UUID;

public interface TokenSignatureService {

    /**
     * Signe un token avec la clé serveur active (TOKEN_SIGNING).
     */
    TokenSignature signToken(Token token) throws BusinessException;

    /**
     * Vérifie la signature d'un token via SignatureVerificationService.
     */
    boolean verifyTokenSignature(Token token) throws BusinessException;

    boolean verifyTokenSignature(OfflineTransactionToken token) throws BusinessException;

    /**
     * Vérifie l'intégrité complète (hash + signature).
     */
    boolean verifyTokenIntegrity(Token token);

    TokenSignature findByTokenId(UUID tokenId);
    void deleteSignature(UUID tokenId);
}