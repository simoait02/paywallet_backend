package com.paylogic.paywalletlite.domain.credit.enums;

/**
 * Enum représentant les différents états possibles d'un remboursement (CreditRepayment)
 * dans le système PayWallet Lite.
 */
public enum RepaymentStatus {

    /** Remboursement planifié mais pas encore dû */
    PENDING,

    /** Remboursement effectué avec succès */
    PAID,

    /** Remboursement en retard par rapport à la date d'échéance */
    OVERDUE,

    /** Remboursement partiel : une partie de la somme due a été payée */
    PARTIAL,

    /** Remboursement annulé par décision administrative (exemple : geste commercial) */
    WAIVED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case PENDING:
                return "Remboursement en attente de la date d'échéance.";
            case PAID:
                return "Remboursement effectué avec succès.";
            case OVERDUE:
                return "Remboursement en retard, la date d'échéance est dépassée.";
            case PARTIAL:
                return "Remboursement partiel, une partie de la dette reste due.";
            case WAIVED:
                return "Remboursement annulé par décision administrative.";
            default:
                return "Statut inconnu.";
        }
    }
}