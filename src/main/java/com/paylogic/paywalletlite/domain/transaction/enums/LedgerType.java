package com.paylogic.paywalletlite.domain.transaction.enums;

/**
 * Enum représentant les différents types de grands livres (Ledger)
 * dans le système PayWallet Lite.
 *
 * Chaque type de ledger a un objectif spécifique et peut être
 * géré indépendamment pour des raisons de performance et de conformité.
 */
public enum LedgerType {

    /** Grand livre général : enregistrement complet de toutes les transactions du système */
    MASTER,

    /** Grand livre spécifique à un wallet : extrait des opérations d'un utilisateur unique */
    WALLET_SPECIFIC,

    /** Grand livre des crédits : suivi des lignes de crédit, décaissements et remboursements */
    CREDIT;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type de ledger.
     */
    public String getDescription() {
        switch (this) {
            case MASTER:
                return "Grand livre général contenant toutes les transactions du système.";
            case WALLET_SPECIFIC:
                return "Grand livre spécifique à un wallet utilisateur.";
            case CREDIT:
                return "Grand livre dédié au suivi des opérations de crédit.";
            default:
                return "Type de ledger inconnu.";
        }
    }
}