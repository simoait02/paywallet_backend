package com.paylogic.paywalletlite.domain.crypto.enums;

/**
 * Enum représentant les différents usages possibles
 * d'une clé cryptographique côté serveur (ServerKey).
 *
 * Chaque clé a un usage unique pour respecter le principe
 * de séparation des privilèges cryptographiques.
 */
public enum ServerKeyPurpose {

    /** Clé utilisée pour signer les tokens offline émis par le backend */
    TOKEN_SIGNING,

    /** Clé utilisée pour signer les réponses d'API (assurance d'intégrité) */
    API_SIGNING,

    /** Clé utilisée pour signer les tokens JWT d'authentification */
    JWT_SIGNING,

    /** Clé utilisée pour signer les certificats utilisateur (PKI interne) */
    CERTIFICATE_SIGNING;

    /**
     * Méthode utilitaire pour obtenir une description lisible de l'usage.
     */
    public String getDescription() {
        switch (this) {
            case TOKEN_SIGNING:
                return "Clé de signature des tokens de paiement offline.";
            case API_SIGNING:
                return "Clé de signature des réponses d'API.";
            case JWT_SIGNING:
                return "Clé de signature des tokens JWT d'authentification.";
            case CERTIFICATE_SIGNING:
                return "Clé de signature des certificats de l'infrastructure PKI.";
            default:
                return "Usage inconnu.";
        }
    }
}