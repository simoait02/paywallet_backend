package com.paylogic.paywalletlite.domain.notification.enums;

/**
 * Enum représentant les différents types d'événements d'audit
 * enregistrés dans le journal d'audit (AuditLog) du système PayWallet Lite.
 *
 * Chaque événement d'audit est immuable et horodaté pour garantir
 * la traçabilité complète de toutes les actions sensibles.
 */
public enum AuditEventType {

    /** Création d'un nouveau token offline par le backend */
    TOKEN_CREATED,

    /** Transfert d'un token d'un wallet à un autre (mode offline) */
    TOKEN_TRANSFERRED,

    /** Utilisation d'un token pour un paiement (le token est dépensé) */
    TOKEN_SPENT,

    /** Réception d'un token par un wallet (mode offline) */
    TOKEN_RECEIVED,

    /** Début d'une session de synchronisation */
    SYNC_INITIATED,

    /** Fin d'une session de synchronisation (succès ou échec) */
    SYNC_COMPLETED,

    /** Authentification réussie d'un utilisateur */
    AUTH_SUCCESS,

    /** Échec d'authentification (mot de passe ou PIN incorrect) */
    AUTH_FAILURE,

    /** Rotation de clé cryptographique effectuée */
    KEY_ROTATION,

    /** Association d'un nouveau device à un compte utilisateur */
    DEVICE_BOUND;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type d'événement.
     */
    public String getDescription() {
        switch (this) {
            case TOKEN_CREATED:
                return "Nouveau token offline créé par le backend.";
            case TOKEN_TRANSFERRED:
                return "Token transféré d'un wallet à un autre.";
            case TOKEN_SPENT:
                return "Token utilisé pour un paiement offline.";
            case TOKEN_RECEIVED:
                return "Token reçu par un wallet.";
            case SYNC_INITIATED:
                return "Session de synchronisation démarrée.";
            case SYNC_COMPLETED:
                return "Session de synchronisation terminée.";
            case AUTH_SUCCESS:
                return "Authentification utilisateur réussie.";
            case AUTH_FAILURE:
                return "Échec d'authentification.";
            case KEY_ROTATION:
                return "Rotation de clé cryptographique effectuée.";
            case DEVICE_BOUND:
                return "Nouveau device associé au compte.";
            default:
                return "Type d'événement inconnu.";
        }
    }
}