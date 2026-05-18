package com.paylogic.paywalletlite.domain.credit.enums;

/**
 * Enum représentant les différents états possibles d'une ligne de crédit (CreditLine)
 * dans le système PayWallet Lite.
 *
 * Cycle de vie :
 * ACTIVE → SPENT (crédit épuisé) → PAID_OFF (remboursé)
 * ACTIVE → EXPIRED (non utilisé dans le délai)
 */
public enum CreditStatus {

    /** Ligne de crédit accordée mais pas encore utilisée */
    UNUSED,

    /** Ligne de crédit utilisée en totalité ou partiellement pour des paiements offline */
    SPENT,

    /** Ligne de crédit expirée avant utilisation ou remboursement complet */
    EXPIRED,

    /** Ligne de crédit intégralement remboursée */
    PAID_OFF;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case UNUSED:
                return "La ligne de crédit est disponible et n'a pas encore été utilisée.";
            case SPENT:
                return "La ligne de crédit a été utilisée pour des paiements offline.";
            case EXPIRED:
                return "La ligne de crédit a expiré avant d'être remboursée intégralement.";
            case PAID_OFF:
                return "La ligne de crédit est intégralement remboursée.";
            default:
                return "Statut inconnu.";
        }
    }
}