package com.paylogic.paywalletlite.security.crypto;

import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * Utilitaire de génération de paires de clés cryptographiques.
 * Supporte ECDSA (P-256, P-384, P-521) pour la signature des tokens.
 */
@Component
public class KeyGeneratorUtil {

    private static final String ECDSA_CURVE = "secp256r1"; // P-256 (standard pour tokens)
    private static final String ECDSA_ALGORITHM = "EC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    /**
     * Génère une paire de clés ECDSA P-256.
     */
    public KeyPair generateEcdsaKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ECDSA_ALGORITHM);
            ECGenParameterSpec ecSpec = new ECGenParameterSpec(ECDSA_CURVE);
            keyGen.initialize(ecSpec, new SecureRandom());
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate ECDSA key pair", e);
        }
    }

    /**
     * Encode une clé publique en Base64 (format PEM-like).
     */
    public String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Encode une clé privée en Base64.
     */
    public String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Génère une paire de clés et retourne les deux en Base64.
     */
    public KeyPairEncoded generateEncodedKeyPair() {
        KeyPair pair = generateEcdsaKeyPair();
        return new KeyPairEncoded(
                encodePublicKey(pair.getPublic()),
                encodePrivateKey(pair.getPrivate())
        );
    }

    // Classe interne pour transport
    public static class KeyPairEncoded {
        private final String publicKeyBase64;
        private final String privateKeyBase64;

        public KeyPairEncoded(String publicKeyBase64, String privateKeyBase64) {
            this.publicKeyBase64 = publicKeyBase64;
            this.privateKeyBase64 = privateKeyBase64;
        }

        public String getPublicKeyBase64() { return publicKeyBase64; }
        public String getPrivateKeyBase64() { return privateKeyBase64; }
    }
}