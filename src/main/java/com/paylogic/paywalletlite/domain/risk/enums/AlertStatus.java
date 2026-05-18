package com.paylogic.paywalletlite.domain.risk.enums;

/**
 * Enum représentant les différents états de traitement d'une alerte de fraude.
 *
 * Cycle de vie :
 * OPEN → INVESTIGATING → RESOLVED | FALSE_POSITIVE
 * OPEN → ESCALATED
 */
public enum AlertStatus {

    /** Alerte ouverte, en attente de prise en charge */
    OPEN,

    /** Alerte en cours d'investigation par l'équipe risque */
    INVESTIGATING,

    /** Alerte résolue : fraude confirmée et action corrective appliquée */
    RESOLVED,

    /** Alerte classée comme faux positif après investigation */
    FALSE_POSITIVE,

    /** Alerte escaladée à un niveau hiérarchique supérieur */
    ESCALATED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case OPEN:
                return "Alerte ouverte en attente de traitement.";
            case INVESTIGATING:
                return "Alerte en cours d'investigation.";
            case RESOLVED:
                return "Alerte résolue avec action corrective.";
            case FALSE_POSITIVE:
                return "Alerte classée comme faux positif.";
            case ESCALATED:
                return "Alerte escaladée au niveau supérieur.";
            default:
                return "Statut d'alerte inconnu.";
        }
    }
}