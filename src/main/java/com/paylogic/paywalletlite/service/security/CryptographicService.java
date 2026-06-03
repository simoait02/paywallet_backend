package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.List;
import java.util.UUID;

public interface CryptographicService {

    /**
     * Génère une nouvelle paire de clés serveur pour un usage donné.
     */
    ServerKey generateKeyPair(ServerKeyPurpose purpose, UUID walletId) throws BusinessException;

    /**
     * Signe des données avec une clé serveur spécifique.
     */
    String signData(String data, UUID serverKeyId) throws BusinessException;

    /**
     * Récupère la clé active pour un usage donné.
     */
    ServerKey getActiveKey(ServerKeyPurpose purpose) throws BusinessException;

    /**
     * Récupère la clé par ID.
     */
    ServerKey findById(UUID serverKeyId) throws BusinessException;

    /**
     * Récupère la clé privée déchiffrée (pour signature).
     */
    String getDecryptedPrivateKey(UUID serverKeyId) throws BusinessException;

    /**
     * Récupère la clé publique (pour distribution).
     */
    String getPublicKey(UUID serverKeyId) throws BusinessException;

    /**
     * Liste toutes les clés par usage.
     */
    List<ServerKey> findByPurpose(ServerKeyPurpose purpose);

    /**
     * Liste les clés par status.
     */
    List<ServerKey> findByStatus(ServerKeyStatus status);

    /**
     * Révoque une clé.
     */
    void revokeKey(UUID serverKeyId, String reason) throws BusinessException;

    /**
     * Supprime une clé (uniquement si expirée/révoquée).
     */
    void deleteKey(UUID serverKeyId) throws BusinessException;
}