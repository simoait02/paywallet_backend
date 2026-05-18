package com.paylogic.paywalletlite.domain.identity.enums;

/**
 * Enum représentant les différents états possibles d'une session utilisateur
 * (DeviceSession) dans le système PayWallet Lite.
 *
 * Une session est créée lors de l'authentification et gérée via JWT.
 */
public enum SessionStatus {

    /** Session active : l'utilisateur est authentifié et peut interagir avec l'API */
    ACTIVE,

    /** Session expirée automatiquement après la durée configurée sans activité */
    EXPIRED,

    /**
     * Session révoquée manuellement (exemple : déconnexion explicite,
     * révocation par l'administrateur, ou détection d'activité suspecte).
     */
    REVOKED,

    /**
     * Session compromise : activité anormale détectée (exemple : IP différente,
     * fingerprint différent). La session est immédiatement invalidée.
     */
    COMPROMISED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "La session est active et l'utilisateur est authentifié.";
            case EXPIRED:
                return "La session a expiré après une période d'inactivité.";
            case REVOKED:
                return "La session a été révoquée manuellement.";
            case COMPROMISED:
                return "La session est compromise et a été invalidée.";
            default:
                return "Statut inconnu.";
        }
    }
}