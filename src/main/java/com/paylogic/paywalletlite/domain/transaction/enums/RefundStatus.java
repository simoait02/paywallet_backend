package com.paylogic.paywalletlite.domain.transaction.enums;

/**
 * Enum représentant les différents états possibles d'un remboursement (TransactionRefund)
 * dans le système PayWallet Lite.
 */
public enum RefundStatus {

    /** Remboursement en attente de traitement */
    PENDING,

    /** Remboursement traité avec succès, fonds crédités au wallet destinataire */
    PROCESSED,

    /** Remboursement échoué (exemple : wallet destinataire fermé) */
    FAILED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case PENDING:
                return "Le remboursement est en attente de traitement.";
            case PROCESSED:
                return "Le remboursement a été effectué avec succès.";
            case FAILED:
                return "Le remboursement a échoué.";
            default:
                return "Statut inconnu.";
        }
    }
}