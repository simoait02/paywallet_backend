package com.paylogic.paywalletlite.domain.token.enums;

/**
 * Enum représentant les différents états possibles
 * d'une configuration d'allocation de tokens (TokenAllocationConfig).
 *
 * Les configurations sont versionnées pour permettre l'évolution
 * des algorithmes de génération de tokens sans impacter les tokens existants.
 */
public enum TokenAllocationConfigStatus {

    /** Configuration active utilisée pour les nouvelles allocations de tokens */
    ACTIVE,

    /** Configuration obsolète, remplacée par une version plus récente, conservée pour l'historique */
    DEPRECATED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "La configuration est active et utilisée pour les nouvelles allocations.";
            case DEPRECATED:
                return "La configuration est obsolète et conservée pour l'audit.";
            default:
                return "Statut inconnu.";
        }
    }
}