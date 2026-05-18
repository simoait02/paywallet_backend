package com.paylogic.paywalletlite.domain.transaction.enums;

/**
 * Enum représentant les différents états possibles d'un surpaiement (overpayment)
 * détecté lors de la synchronisation d'une transaction offline.
 *
 * Un surpaiement se produit quand le payeur transfère des tokens
 * dont la valeur totale dépasse le montant dû (exemple : tokens de 100 + 50
 * pour un paiement de 120, overpayment de 30).
 */
public enum OverpaymentStatus {

    /** Surpaiement détecté, remboursement en attente de traitement */
    PENDING_REFUND,

    /** Surpaiement remboursé au payeur */
    REFUNDED,

    /**
     * Surpaiement non réclamé dans le délai imparti,
     * conservé par le système (selon la politique définie).
     */
    FORFEITED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case PENDING_REFUND:
                return "Le remboursement du surpaiement est en attente.";
            case REFUNDED:
                return "Le surpaiement a été remboursé au payeur.";
            case FORFEITED:
                return "Le surpaiement non réclamé a été conservé par le système.";
            default:
                return "Statut inconnu.";
        }
    }
}