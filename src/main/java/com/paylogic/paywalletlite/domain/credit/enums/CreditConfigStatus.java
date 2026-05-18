package com.paylogic.paywalletlite.domain.credit.enums;

/**
 * Enum représentant les différents états possibles
 * d'une configuration de crédit (CreditConfig).
 *
 * Les configurations sont versionnées et une seule est ACTIVE à la fois.
 */
public enum CreditConfigStatus {

    /** Configuration active utilisée pour l'octroi de nouvelles lignes de crédit */
    ACTIVE,

    /** Configuration obsolète, remplacée par une version plus récente */
    DEPRECATED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "La configuration de crédit est active.";
            case DEPRECATED:
                return "La configuration de crédit est obsolète.";
            default:
                return "Statut inconnu.";
        }
    }
}