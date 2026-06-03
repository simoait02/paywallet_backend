package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.crypto.ServerKeyRepository;
import com.paylogic.paywalletlite.security.crypto.AesEncryptionUtil;
import com.paylogic.paywalletlite.security.crypto.EcdsaSignatureUtil;
import com.paylogic.paywalletlite.security.crypto.KeyGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CryptographicServiceImpl implements CryptographicService {

    private static final String KEY_ALGORITHM = "ECDSA_P256";
    private static final int DEFAULT_KEY_LIFETIME_DAYS = 365;

    private final ServerKeyRepository serverKeyRepository;
    private final KeyGeneratorUtil keyGeneratorUtil;
    private final AesEncryptionUtil aesEncryptionUtil;
    private final EcdsaSignatureUtil ecdsaSignatureUtil;


    @Value("${crypto.key.lifetime.days:365}")
    private int keyLifetimeDays;

    @Autowired
    public CryptographicServiceImpl(ServerKeyRepository serverKeyRepository,
                                    KeyGeneratorUtil keyGeneratorUtil,
                                    AesEncryptionUtil aesEncryptionUtil,
                                    EcdsaSignatureUtil ecdsaSignatureUtil) {
        this.serverKeyRepository = serverKeyRepository;
        this.keyGeneratorUtil = keyGeneratorUtil;
        this.aesEncryptionUtil = aesEncryptionUtil;
        this.ecdsaSignatureUtil = ecdsaSignatureUtil;
    }

    @Override
    @Transactional
    public ServerKey generateKeyPair(ServerKeyPurpose purpose, UUID walletId) throws BusinessException {
        // Vérifier qu'une clé active n'existe pas déjà pour ce purpose (sauf TOKEN_SIGNING)
        if (purpose != ServerKeyPurpose.TOKEN_SIGNING) {
            if (serverKeyRepository.existsByPurposeAndStatus(purpose, ServerKeyStatus.ACTIVE)) {
                throw new BusinessException("Active key already exists for purpose: " + purpose);
            }
        }

        // Générer la paire de clés
        KeyGeneratorUtil.KeyPairEncoded keyPair = keyGeneratorUtil.generateEncodedKeyPair();

        // Chiffrer la clé privée
        String encryptedPrivateKey = aesEncryptionUtil.encrypt(keyPair.getPrivateKeyBase64());

        // Créer l'entité
        ServerKey serverKey = new ServerKey();
        serverKey.setKeyPurpose(purpose);
        serverKey.setPublicKeyPem(keyPair.getPublicKeyBase64());
        serverKey.setPrivateKeyEncrypted(encryptedPrivateKey);
        serverKey.setKeyAlgorithm(KEY_ALGORITHM);
        serverKey.setCreatedAt(LocalDateTime.now());
        serverKey.setExpiresAt(LocalDateTime.now().plusDays(keyLifetimeDays));
        serverKey.setStatus(ServerKeyStatus.ACTIVE);

        return serverKeyRepository.save(serverKey);
    }

    @Override
    public String signData(String data, UUID serverKeyId) throws BusinessException {
        String privateKey = getDecryptedPrivateKey(serverKeyId);
        return ecdsaSignatureUtil.sign(data, privateKey);
    }

    @Override
    public ServerKey getActiveKey(ServerKeyPurpose purpose) throws BusinessException {
        return serverKeyRepository.findActiveByPurpose(purpose)
                .orElseThrow(() -> new BusinessException("No active key found for purpose: " + purpose));
    }

    @Override
    public ServerKey findById(UUID serverKeyId) throws BusinessException {
        return serverKeyRepository.findById(serverKeyId)
                .orElseThrow(() -> new BusinessException("Server key not found: " + serverKeyId));
    }

    @Override
    public String getDecryptedPrivateKey(UUID serverKeyId) throws BusinessException {
        ServerKey key = findById(serverKeyId);

        if (key.getStatus() == ServerKeyStatus.REVOKED || key.getStatus() == ServerKeyStatus.EXPIRED) {
            throw new BusinessException("Key is not active: " + serverKeyId);
        }

        return aesEncryptionUtil.decrypt(key.getPrivateKeyEncrypted());
    }

    @Override
    public String getPublicKey(UUID serverKeyId) throws BusinessException {
        return findById(serverKeyId).getPublicKeyPem();
    }

    @Override
    public List<ServerKey> findByPurpose(ServerKeyPurpose purpose) {
        return serverKeyRepository.findByPurpose(purpose);
    }

    @Override
    public List<ServerKey> findByStatus(ServerKeyStatus status) {
        return serverKeyRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void revokeKey(UUID serverKeyId, String reason) throws BusinessException {
        ServerKey key = findById(serverKeyId);

        if (key.getStatus() == ServerKeyStatus.REVOKED) {
            throw new BusinessException("Key already revoked: " + serverKeyId);
        }

        key.setStatus(ServerKeyStatus.REVOKED);
        serverKeyRepository.save(key);
    }

    @Override
    @Transactional
    public void deleteKey(UUID serverKeyId) throws BusinessException {
        ServerKey key = findById(serverKeyId);

        if (key.getStatus() == ServerKeyStatus.ACTIVE) {
            throw new BusinessException("Cannot delete active key: " + serverKeyId);
        }

        serverKeyRepository.delete(key);
    }
}