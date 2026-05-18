package com.paylogic.paywalletlite.domain.wallet.enums;

/**
 * Enum représentant les différents états possibles d'un portefeuille (wallet)
 * dans le système PayWallet Lite.
 *
 * Cycle de vie :
 * ACTIVE ↔ LOCKED → CLOSED
 * ACTIVE → FROZEN (gel administratif)
 */
public enum WalletStatus {

    /** Portefeuille actif : toutes les opérations sont autorisées */
    ACTIVE,

    /**
     * Portefeuille verrouillé temporairement (exemple : suspicion de fraude,
     * trop de tentatives de PIN échouées, demande utilisateur).
     * Les transactions sont bloquées mais le wallet peut être déverrouillé.
     */
    LOCKED,

    /** Portefeuille définitivement fermé. Action irréversible. */
    CLOSED,

    /**
     * Portefeuille gelé par décision administrative ou judiciaire.
     * Les fonds sont conservés mais inaccessibles jusqu'à résolution.
     */
    FROZEN;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "Le portefeuille est actif et toutes les opérations sont autorisées.";
            case LOCKED:
                return "Le portefeuille est temporairement verrouillé et peut être débloqué.";
            case CLOSED:
                return "Le portefeuille est définitivement fermé. Action irréversible.";
            case FROZEN:
                return "Le portefeuille est gelé par décision administrative ou judiciaire.";
            default:
                return "Statut inconnu.";
        }
    }
}