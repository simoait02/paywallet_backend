package com.paylogic.paywalletlite.domain.transaction.enums;

/**
 * Enum représentant les différents types d'écritures comptables
 * dans le grand livre (Ledger) du système PayWallet Lite.
 *
 * Basé sur le principe de la comptabilité en partie double :
 * chaque transaction génère au moins une écriture DEBIT et une écriture CREDIT.
 */
public enum EntryType {

    /** Sortie d'argent : le solde du wallet diminue */
    DEBIT,

    /** Entrée d'argent : le solde du wallet augmente */
    CREDIT,

    /**
     * Gel temporaire de fonds : l'argent n'est pas débité mais réservé
     * (utilisé lors de l'allocation conditionnelle de tokens).
     */
    HOLD,

    /**
     * Libération d'un gel : les fonds précédemment gelés (HOLD)
     * sont à nouveau disponibles.
     */
    RELEASE;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type d'écriture.
     */
    public String getDescription() {
        switch (this) {
            case DEBIT:
                return "Débit : diminution du solde du wallet.";
            case CREDIT:
                return "Crédit : augmentation du solde du wallet.";
            case HOLD:
                return "Gel temporaire de fonds sans débit effectif.";
            case RELEASE:
                return "Libération de fonds précédemment gelés.";
            default:
                return "Type d'écriture inconnu.";
        }
    }
}