package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.crypto.ServerKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class KeyRotationServiceImpl implements KeyRotationService {

    private static final int ROTATION_THRESHOLD_DAYS = 30;

    private final ServerKeyRepository serverKeyRepository;
    private final CryptographicService cryptographicService;

    @Value("${crypto.key.rotation.threshold.days:30}")
    private int rotationThresholdDays;

    @Autowired
    public KeyRotationServiceImpl(ServerKeyRepository serverKeyRepository,
                                  CryptographicService cryptographicService) {
        this.serverKeyRepository = serverKeyRepository;
        this.cryptographicService = cryptographicService;
    }

    @Override
    @Transactional
    public ServerKey rotateKey(ServerKeyPurpose purpose) throws BusinessException {
        // Récupérer la clé active actuelle
        ServerKey currentKey = cryptographicService.getActiveKey(purpose);

        // Marquer l'ancienne clé comme PENDING_ROTATION
        currentKey.setStatus(ServerKeyStatus.PENDING_ROTATION);
        currentKey.setRotatedAt(LocalDateTime.now());
        serverKeyRepository.save(currentKey);

        // Générer la nouvelle clé
        ServerKey newKey = cryptographicService.generateKeyPair(purpose, currentKey.getWalletId());

        // Activer la nouvelle clé
        newKey.setStatus(ServerKeyStatus.ACTIVE);
        serverKeyRepository.save(newKey);

        // Expirer l'ancienne clé après une période de grâce
        // (laisser l'ancienne active pendant 24h pour les tokens en vol)

        return newKey;
    }

    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Tous les jours à 2h du matin
    @Transactional
    public void checkAndRotateExpiringKeys() {
        List<ServerKey> expiringKeys = findKeysNeedingRotation();

        for (ServerKey key : expiringKeys) {
            try {
                rotateKey(key.getKeyPurpose());
            } catch (BusinessException e) {
                System.err.println("Failed to rotate key " + key.getServerKeyId() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public List<ServerKey> findKeysNeedingRotation() {
        LocalDateTime threshold = LocalDateTime.now().plusDays(rotationThresholdDays);
        return serverKeyRepository.findExpiringKeys(threshold);
    }

    @Override
    @Transactional
    public void activateNewKey(UUID newKeyId, UUID oldKeyId) throws BusinessException {
        ServerKey newKey = cryptographicService.findById(newKeyId);
        ServerKey oldKey = cryptographicService.findById(oldKeyId);

        if (newKey.getStatus() != ServerKeyStatus.ACTIVE) {
            newKey.setStatus(ServerKeyStatus.ACTIVE);
            serverKeyRepository.save(newKey);
        }

        oldKey.setStatus(ServerKeyStatus.EXPIRED);
        serverKeyRepository.save(oldKey);
    }
}