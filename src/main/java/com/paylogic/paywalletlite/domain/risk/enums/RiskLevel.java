package com.paylogic.paywalletlite.domain.risk.enums;

/**
 * Enum représentant les différents niveaux de risque
 * attribués à un profil utilisateur (RiskProfile).
 *
 * Le niveau de risque influence les limites de transaction,
 * l'éligibilité au crédit offline, et la sensibilité de la détection de fraude.
 */
public enum RiskLevel {

    /** Risque faible : utilisateur de confiance avec historique positif */
    LOW,

    /** Risque modéré : utilisateur standard sans incident mais sans historique étendu */
    MEDIUM,

    /** Risque élevé : utilisateur avec incidents passés ou comportement atypique */
    HIGH,

    /** Risque critique : fraude avérée ou suspicion forte, accès bloqué */
    CRITICAL;

    /**
     * Méthode utilitaire pour obtenir une description lisible du niveau de risque.
     */
    public String getDescription() {
        switch (this) {
            case LOW:
                return "Niveau de risque faible. Historique utilisateur positif.";
            case MEDIUM:
                return "Niveau de risque modéré. Comportement standard.";
            case HIGH:
                return "Niveau de risque élevé. Incidents ou anomalies détectés.";
            case CRITICAL:
                return "Niveau de risque critique. Suspicion forte de fraude.";
            default:
                return "Niveau de risque inconnu.";
        }
    }
}