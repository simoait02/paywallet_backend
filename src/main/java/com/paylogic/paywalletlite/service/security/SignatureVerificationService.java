package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.exception.BusinessException;

public interface SignatureVerificationService {

    /**
     * Vérifie la signature d'un token avec la clé serveur.
     */
    boolean verifyTokenSignature(Token token) throws BusinessException;

    boolean verifyTokenSignature(OfflineTransactionToken token) throws BusinessException;

    /**
     * Vérifie une signature générique avec une clé publique.
     */
    boolean verifyWithPublicKey(String data, String signature, String publicKeyBase64);

    /**
     * Vérifie une signature de transfert offline.
     */
    boolean verifyOfflineTransfer(String payload, String signature, String payerPublicKey);

    /**
     * Vérifie l'intégrité d'une chaîne de certificats.
     */
    boolean verifyCertificateChain(String certificatePem, String caPublicKey);
}