package com.paylogic.paywalletlite.domain.identity.enums;

/**
 * Enum représentant les différents états possibles d'un compte utilisateur
 * dans le système PayWallet Lite.
 *
 * Un compte utilisateur suit un cycle de vie :
 * ACTIVE → SUSPENDED (temporaire) ou CLOSED (définitif)
 */
public enum AccountStatus {

    /** Compte actif : l'utilisateur peut se connecter et effectuer toutes les opérations autorisées */
    ACTIVE,

    /**
     * Compte suspendu temporairement (exemple : après 3 échecs d'authentification,
     * demande de l'utilisateur, ou suspicion de fraude sans confirmation).
     * L'utilisateur ne peut pas se connecter mais le compte peut être réactivé.
     */
    SUSPENDED,

    /**
     * Compte définitivement fermé (exemple : demande de l'utilisateur, décision administrative).
     * Cette action est irréversible. Les données sont conservées pour l'audit.
     */
    CLOSED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "Le compte est actif et pleinement opérationnel.";
            case SUSPENDED:
                return "Le compte est temporairement suspendu et peut être réactivé.";
            case CLOSED:
                return "Le compte est définitivement fermé. Action irréversible.";
            default:
                return "Statut inconnu.";
        }
    }
}