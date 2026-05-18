package com.paylogic.paywalletlite.domain.risk.enums;

/**
 * Enum représentant les différents types d'alertes de fraude
 * pouvant être déclenchées par le système PayWallet Lite.
 */
public enum AlertType {

    /** Tentative de double dépense d'un token déjà racheté */
    DOUBLE_SPEND_ATTEMPT,

    /** Vélocité de transaction anormalement élevée (nombre de transactions par minute/heure) */
    VELOCITY_EXCEEDED,

    /** Schéma de transaction inhabituel détecté par l'IA (montants, horaires, destinataires) */
    UNUSUAL_PATTERN,

    /** Incohérence entre le device utilisé et le device enregistré (device binding) */
    DEVICE_MISMATCH,

    /** Anomalie géographique : transactions depuis des localisations incompatibles */
    GEO_ANOMALY,

    /** Tentative de rachat d'un token expiré */
    EXPIRED_TOKEN_REDEMPTION;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type d'alerte.
     */
    public String getDescription() {
        switch (this) {
            case DOUBLE_SPEND_ATTEMPT:
                return "Tentative de double dépense d'un token détectée.";
            case VELOCITY_EXCEEDED:
                return "Fréquence de transactions anormalement élevée.";
            case UNUSUAL_PATTERN:
                return "Schéma de transaction inhabituel détecté.";
            case DEVICE_MISMATCH:
                return "Incohérence entre le device utilisé et le device enregistré.";
            case GEO_ANOMALY:
                return "Transactions depuis des localisations géographiques incompatibles.";
            case EXPIRED_TOKEN_REDEMPTION:
                return "Tentative de rachat d'un token expiré.";
            default:
                return "Type d'alerte inconnu.";
        }
    }
}