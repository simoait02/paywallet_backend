package com.paylogic.paywalletlite.domain.identity.enums;

/**
 * Enum représentant les différents niveaux de vérification KYC
 * (Know Your Customer) d'un utilisateur.
 *
 * Le niveau KYC détermine les limites de transaction et l'accès
 * aux fonctionnalités avancées (crédit offline, limites élevées).
 */
public enum KYCStatus {

    /**
     * Vérification KYC complétée avec succès.
     * L'utilisateur a fourni les documents requis et ils ont été validés.
     */
    VERIFIED,

    /**
     * Vérification KYC en attente : les documents ont été soumis
     * mais n'ont pas encore été examinés, ou la vérification est incomplète.
     */
    PENDING;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut KYC.
     */
    public String getDescription() {
        switch (this) {
            case VERIFIED:
                return "L'identité de l'utilisateur a été vérifiée avec succès.";
            case PENDING:
                return "La vérification d'identité est en attente de traitement.";
            default:
                return "Statut inconnu.";
        }
    }
}