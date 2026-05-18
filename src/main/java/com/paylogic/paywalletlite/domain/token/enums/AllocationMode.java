package com.paylogic.paywalletlite.domain.token.enums;

/**
 * Enum représentant les différents modes d'allocation de tokens offline
 * dans le système PayWallet Lite.
 *
 * Chaque mode détermine comment les fonds sont réservés ou garantis
 * lors de la création des tokens.
 */
public enum AllocationMode {

    /**
     * Réservation explicite : l'utilisateur demande manuellement l'allocation
     * d'un montant spécifique. Les fonds sont immédiatement débités du solde online
     * et convertis en tokens. Garantie totale de valeur.
     */
    EXPLICIT_RESERVATION,

    /**
     * Réservation conditionnelle : le système pré-alloue automatiquement des tokens
     * basés sur l'historique de l'utilisateur. Les fonds ne sont pas débités immédiatement
     * mais réservés conditionnellement. Si l'utilisateur dépense le solde en ligne,
     * les tokens conditionnels deviennent invalides.
     */
    CONDITIONAL_RESERVATION,

    /**
     * Allocation basée sur le crédit : les tokens sont émis sans réservation de fonds.
     * L'utilisateur bénéficie d'une ligne de crédit et devra rembourser
     * lors de la synchronisation. Soumis à des critères d'éligibilité stricts.
     */
    CREDIT_BASED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du mode d'allocation.
     */
    public String getDescription() {
        switch (this) {
            case EXPLICIT_RESERVATION:
                return "Allocation manuelle avec débit immédiat du solde online.";
            case CONDITIONAL_RESERVATION:
                return "Pré-allocation automatique conditionnelle à la disponibilité des fonds.";
            case CREDIT_BASED:
                return "Allocation basée sur le crédit, remboursement différé.";
            default:
                return "Mode d'allocation inconnu.";
        }
    }
}