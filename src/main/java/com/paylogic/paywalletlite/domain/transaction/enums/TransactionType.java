package com.paylogic.paywalletlite.domain.transaction.enums;

/**
 * Enum représentant les différents types de transactions financières
 * possibles dans le système PayWallet Lite.
 *
 * Chaque type détermine le traitement comptable et les règles de validation.
 */
public enum TransactionType {

    /** Transfert d'argent entre deux wallets utilisateurs (Peer-to-Peer) */
    P2P_TRANSFER,

    /** Paiement d'un client vers un commerçant */
    MERCHANT_PAYMENT,

    /** Allocation de fonds du solde online vers des tokens offline */
    TOKEN_ALLOCATION,

    /** Rachat de tokens offline et conversion en solde online */
    TOKEN_REDEMPTION,

    /** Octroi de crédit offline (mode CREDIT_BASED) */
    ALLOCATED_CREDIT,

    /** Remboursement d'une ligne de crédit par l'utilisateur */
    CREDIT_REPAYMENT,

    /** Frais de service prélevés par le système */
    FEE_CHARGE,

    /** Remboursement d'un trop-perçu (overpayment) lors de la synchronisation */
    REFUND;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type de transaction.
     */
    public String getDescription() {
        switch (this) {
            case P2P_TRANSFER:
                return "Transfert d'argent entre deux wallets utilisateurs.";
            case MERCHANT_PAYMENT:
                return "Paiement d'un client vers un commerçant.";
            case TOKEN_ALLOCATION:
                return "Conversion du solde online en tokens pour usage offline.";
            case TOKEN_REDEMPTION:
                return "Rachat de tokens offline crédité sur le solde online.";
            case ALLOCATED_CREDIT:
                return "Octroi de crédit pour paiement offline.";
            case CREDIT_REPAYMENT:
                return "Remboursement d'une ligne de crédit.";
            case FEE_CHARGE:
                return "Frais de service prélevés par le système.";
            case REFUND:
                return "Remboursement d'un trop-perçu au payeur.";
            default:
                return "Type de transaction inconnu.";
        }
    }
}