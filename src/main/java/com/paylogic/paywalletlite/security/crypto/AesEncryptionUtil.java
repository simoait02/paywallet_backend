package com.paylogic.paywalletlite.security.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Chiffrement AES-256 GCM pour le stockage sécurisé des clés privées.
 *
 * Contraintes :
 * - Clés privées jamais stockées en clair
 * - IV unique par opération de chiffrement
 * - Authentification GCM (intégrité + confidentialité)
 */
@Component
public class AesEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits recommandé par NIST
    private static final int GCM_TAG_LENGTH = 128; // 128 bits
    private static final int AES_KEY_SIZE = 256;

    private final String masterKey;

    public AesEncryptionUtil(@Value("${crypto.master.key:NOT_CONFIGURED}") String masterKey) {
        if ("NOT_CONFIGURED".equals(masterKey) || masterKey.length() < 32) {
            throw new IllegalArgumentException("Master key must be at least 32 characters");
        }
        this.masterKey = masterKey;
    }

    /**
     * Chiffre une clé privée (ou toute donnée sensible) avec AES-256 GCM.
     *
     * Format du résultat : [IV (12 bytes)][ciphertext + tag]
     */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = generateIv();
            SecretKey key = deriveKey(masterKey);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Concaténer IV + ciphertext
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            buffer.put(iv);
            buffer.put(ciphertext);

            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Déchiffre une clé privée chiffrée.
     */
    public String decrypt(String encryptedData) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedData);
            ByteBuffer buffer = ByteBuffer.wrap(decoded);

            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);

            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);

            SecretKey key = deriveKey(masterKey);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Génère un IV aléatoire de 12 octets.
     */
    private byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Dérive une clé AES-256 à partir du master key (simple hash SHA-256).
     * TODO: Remplacer par PBKDF2 ou Argon2 pour production
     */
    private SecretKey deriveKey(String masterKey) {
        byte[] keyBytes = HashUtil.sha256(masterKey).getBytes(StandardCharsets.UTF_8);
        // Tronquer ou étendre à 32 octets (256 bits)
        byte[] aesKey = new byte[32];
        System.arraycopy(keyBytes, 0, aesKey, 0, Math.min(keyBytes.length, 32));
        return new SecretKeySpec(aesKey, ALGORITHM);
    }

    /**
     * Génère un nouveau master key aléatoire (pour initialisation).
     */
    public static String generateMasterKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(AES_KEY_SIZE);
            SecretKey key = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate master key", e);
        }
    }
}