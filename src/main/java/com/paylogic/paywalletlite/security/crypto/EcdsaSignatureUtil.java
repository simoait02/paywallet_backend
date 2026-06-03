package com.paylogic.paywalletlite.security.crypto;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utilitaire de signature et vérification ECDSA.
 *
 * Utilisé pour :
 * - Signer les tokens (server-side)
 * - Vérifier les signatures des transferts offline (client-side)
 */
@Component
public class EcdsaSignatureUtil {

    private static final String ECDSA_ALGORITHM = "EC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    /**
     * Signe des données avec une clé privée ECDSA (Base64).
     */
    public String sign(byte[] data, String privateKeyBase64) {
        try {
            PrivateKey privateKey = decodePrivateKey(privateKeyBase64);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            byte[] signed = signature.sign();
            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            throw new RuntimeException("ECDSA signing failed", e);
        }
    }

    /**
     * Signe une chaîne de caractères.
     */
    public String sign(String data, String privateKeyBase64) {
        return sign(data.getBytes(StandardCharsets.UTF_8), privateKeyBase64);
    }

    /**
     * Vérifie une signature ECDSA.
     */
    public boolean verify(byte[] data, String signatureBase64, String publicKeyBase64) {
        try {
            PublicKey publicKey = decodePublicKey(publicKeyBase64);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(Base64.getDecoder().decode(signatureBase64));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Vérifie une signature à partir d'une chaîne.
     */
    public boolean verify(String data, String signatureBase64, String publicKeyBase64) {
        return verify(data.getBytes(StandardCharsets.UTF_8), signatureBase64, publicKeyBase64);
    }

    /**
     * Décode une clé privée Base64 en objet PrivateKey.
     */
    public PrivateKey decodePrivateKey(String privateKeyBase64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance(ECDSA_ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode private key", e);
        }
    }

    /**
     * Décode une clé publique Base64 en objet PublicKey.
     */
    public PublicKey decodePublicKey(String publicKeyBase64) {
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance(ECDSA_ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode public key", e);
        }
    }

    /**
     * Extrait la clé publique d'une paire (pour dérivation depuis privée).
     * TODO: Implémenter avec BouncyCastle si nécessaire
     */
    public String derivePublicKeyFromPrivate(String privateKeyBase64) {
        // Placeholder - nécessite BouncyCastle ou calcul EC point multiplication
        throw new UnsupportedOperationException("Public key derivation requires BouncyCastle");
    }
}