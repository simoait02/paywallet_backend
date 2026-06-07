package com.paylogic.paywalletlite.domain.wallet.enums;

/**
 * Enum représentant les différents mécanismes de stockage
 * des clés cryptographiques privées côté device.
 *
 * Utilisé pour indiquer où et comment la clé privée du wallet
 * est protégée sur l'appareil mobile.
 */
public enum KeyStorageType {

    /**
     * Clé stockée dans l'Android Keystore, avec support hardware (TEE ou StrongBox).
     * Niveau de sécurité le plus élevé sur Android.
     */
    ANDROID_KEYSTORE,

    /**
     * Clé stockée dans le Keychain iOS, avec support du Secure Enclave.
     * Niveau de sécurité le plus élevé sur iOS.
     */
    APPLE_KEYCHAIN,

    /**
     * Clé encryptée et stocké dans la base de données du server.
     * */
    SERVER_ENCRYPTED,

    /**
     * Platform-agnostic marker: private key lives on the device's secure
     * element (Android Keystore or iOS Secure Enclave). The backend never holds
     * the privkey. Used by the CSR-based wallet creation flow.
     */
    DEVICE_ONLY;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type de stockage.
     */
    public String getDescription() {
        switch (this) {
            case ANDROID_KEYSTORE:
                return "Stockage sécurisé via Android Keystore (TEE/StrongBox).";
            case APPLE_KEYCHAIN:
                return "Stockage sécurisé via Apple Keychain et Secure Enclave.";
            case SERVER_ENCRYPTED:
                return "Stockage sécurisé dans la base données du server";
            case DEVICE_ONLY:
                return "Private key resident on the device's secure element (platform-agnostic).";
            default:
                return "Type de stockage inconnu.";
        }
    }
}