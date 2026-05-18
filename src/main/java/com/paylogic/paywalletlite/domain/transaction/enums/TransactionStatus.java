package com.paylogic.paywalletlite.domain.transaction.enums;

/**
 * Enum représentant les différents états possibles d'une transaction
 * dans le système PayWallet Lite.
 *
 * Cycle de vie :
 * PENDING → COMPLETED | FAILED | EXPIRED
 * OFFLINE_PENDING → COMPLETED | FAILED (après synchronisation)
 * COMPLETED → DISPUTED → REVERSED
 */
public enum TransactionStatus {

    /** Transaction en attente de traitement (mode online) */
    PENDING,

    /** Transaction initiée en mode offline, en attente de synchronisation */
    OFFLINE_PENDING,

    /** Transaction traitée avec succès et enregistrée dans le ledger */
    COMPLETED,

    /** Transaction échouée (exemple : fonds insuffisants, signature invalide) */
    FAILED,

    /** Transaction expirée avant traitement (exemple : token expiré) */
    EXPIRED,

    /** Transaction contestée par l'une des parties */
    DISPUTED,

    /** Transaction annulée et fonds restitués (exemple : résolution de litige) */
    REVERSED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case PENDING:
                return "Transaction en attente de traitement par le backend.";
            case OFFLINE_PENDING:
                return "Transaction offline en attente de synchronisation.";
            case COMPLETED:
                return "Transaction traitée avec succès et enregistrée.";
            case FAILED:
                return "Transaction échouée pour cause technique ou métier.";
            case EXPIRED:
                return "Transaction expirée avant d'avoir pu être traitée.";
            case DISPUTED:
                return "Transaction contestée et en cours d'investigation.";
            case REVERSED:
                return "Transaction annulée et fonds restitués.";
            default:
                return "Statut inconnu.";
        }
    }
}