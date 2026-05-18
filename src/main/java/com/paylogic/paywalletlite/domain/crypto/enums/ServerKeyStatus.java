package com.paylogic.paywalletlite.domain.crypto.enums;

/**
 * Enum représentant les différents états possibles
 * d'une clé serveur (ServerKey) utilisée pour la signature des tokens,
 * des API, ou des certificats.
 *
 * Cycle de vie :
 * ACTIVE → PENDING_ROTATION → EXPIRED
 * ACTIVE → REVOKED (compromission)
 */
public enum ServerKeyStatus {

    /** Clé serveur active et utilisable pour les opérations de signature */
    ACTIVE,

    /** Clé serveur expirée, remplacée par une nouvelle clé */
    EXPIRED,

    /** Clé serveur révoquée (exemple : compromission suspectée) */
    REVOKED,

    /**
     * Clé serveur en attente de rotation.
     * L'ancienne clé reste active pour vérifier les signatures existantes
     * pendant que la nouvelle clé est déployée.
     */
    PENDING_ROTATION;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "La clé serveur est active et utilisable.";
            case EXPIRED:
                return "La clé serveur a expiré et n'est plus utilisée.";
            case REVOKED:
                return "La clé serveur a été révoquée.";
            case PENDING_ROTATION:
                return "La clé serveur est en cours de rotation.";
            default:
                return "Statut inconnu.";
        }
    }
}