package com.paylogic.paywalletlite.domain.risk.enums;

/**
 * Enum représentant les niveaux de sévérité d'une alerte de fraude.
 *
 * La sévérité détermine l'action automatique entreprise :
 * - LOW : journalisation uniquement
 * - MEDIUM : notification à l'équipe risque
 * - HIGH : blocage temporaire du wallet
 * - CRITICAL : blocage immédiat + escalade
 */
public enum AlertSeverity {

    /** Alerte informative sans action automatique */
    LOW,

    /** Alerte nécessitant une revue humaine dans les 24h */
    MEDIUM,

    /** Alerte grave avec blocage temporaire automatique */
    HIGH,

    /** Alerte critique avec blocage immédiat et escalade */
    CRITICAL;

    /**
     * Méthode utilitaire pour obtenir une description lisible du niveau de sévérité.
     */
    public String getDescription() {
        switch (this) {
            case LOW:
                return "Alerte informative, pas d'action automatique requise.";
            case MEDIUM:
                return "Alerte nécessitant une revue humaine sous 24h.";
            case HIGH:
                return "Alerte grave avec blocage temporaire automatique.";
            case CRITICAL:
                return "Alerte critique avec blocage immédiat et escalade.";
            default:
                return "Niveau de sévérité inconnu.";
        }
    }
}