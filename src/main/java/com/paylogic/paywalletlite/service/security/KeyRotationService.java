package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.List;
import java.util.UUID;

public interface KeyRotationService {

    /**
     * Effectue la rotation d'une clé spécifique.
     */
    ServerKey rotateKey(ServerKeyPurpose purpose) throws BusinessException;

    /**
     * Vérifie et rotate les clés proches de l'expiration.
     */
    void checkAndRotateExpiringKeys();

    /**
     * Liste les clés nécessitant une rotation.
     */
    List<ServerKey> findKeysNeedingRotation();

    /**
     * Active une nouvelle clé et désactive l'ancienne.
     */
    void activateNewKey(UUID newKeyId, UUID oldKeyId) throws BusinessException;
}