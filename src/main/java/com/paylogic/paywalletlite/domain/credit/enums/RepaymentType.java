package com.paylogic.paywalletlite.domain.credit.enums;

/**
 * Enum représentant les différents modes de remboursement
 * d'une ligne de crédit (CreditRepayment).
 */
public enum RepaymentType {

    /** Remboursement initié manuellement par l'utilisateur */
    MANUAL,

    /** Remboursement automatique prélevé sur le solde online dès qu'il est suffisant */
    AUTO_DEDUCTION;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type de remboursement.
     */
    public String getDescription() {
        switch (this) {
            case MANUAL:
                return "Remboursement initié manuellement par l'utilisateur.";
            case AUTO_DEDUCTION:
                return "Remboursement automatique par prélèvement sur le solde.";
            default:
                return "Type de remboursement inconnu.";
        }
    }
}