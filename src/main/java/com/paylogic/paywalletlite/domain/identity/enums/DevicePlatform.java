package com.paylogic.paywalletlite.domain.identity.enums;

/**
 * Enum représentant les plateformes mobiles supportées
 * par l'application PayWallet Lite.
 *
 * Utilisé pour adapter les mécanismes de sécurité
 * (Android Keystore vs iOS Keychain/Secure Enclave).
 */
public enum DevicePlatform {

    /** Appareil fonctionnant sous Android (API 26 minimum pour Keystore/TEE) */
    ANDROID,

    /** Appareil fonctionnant sous iOS (Secure Enclave pour les clés cryptographiques) */
    IOS;

    /**
     * Méthode utilitaire pour obtenir une description lisible de la plateforme.
     */
    public String getDescription() {
        switch (this) {
            case ANDROID:
                return "Plateforme Android utilisant Android Keystore pour le stockage sécurisé.";
            case IOS:
                return "Plateforme iOS utilisant Keychain et Secure Enclave.";
            default:
                return "Plateforme inconnue.";
        }
    }
}