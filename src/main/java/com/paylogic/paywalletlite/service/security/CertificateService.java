package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.Certificate;
import com.paylogic.paywalletlite.domain.crypto.CertificateAuthority;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.List;
import java.util.UUID;

public interface CertificateService {

    /**
     * Émet un nouveau certificat pour un wallet.
     */
    Certificate issueCertificate(UUID walletId, UUID caId) throws BusinessException;

    /**
     * Valide un certificat (non révoqué, non expiré).
     */
    boolean validateCertificate(UUID certificateId) throws BusinessException;

    /**
     * Révoque un certificat.
     */
    void revokeCertificate(UUID certificateId, String reason) throws BusinessException;

    /**
     * Récupère un certificat par ID.
     */
    Certificate findById(UUID certificateId) throws BusinessException;

    /**
     * Récupère les certificats d'un wallet.
     */
    List<Certificate> findByWalletId(UUID walletId);

    /**
     * Récupère la CA active.
     */
    CertificateAuthority getActiveCA() throws BusinessException;

    /**
     * Initialise la CA racine (si inexistante).
     */
    CertificateAuthority initializeRootCA(String caName) throws BusinessException;
}