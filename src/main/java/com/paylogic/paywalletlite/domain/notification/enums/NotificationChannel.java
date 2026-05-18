package com.paylogic.paywalletlite.domain.notification.enums;

/**
 * Enum représentant les canaux de distribution des notifications.
 *
 * Les notifications peuvent être envoyées via un ou plusieurs canaux
 * selon les préférences de l'utilisateur et la criticité du message.
 */
public enum NotificationChannel {

    /** Notification push via Firebase Cloud Messaging (Android) ou APNs (iOS) */
    PUSH,

    /** SMS envoyé au numéro de téléphone enregistré */
    SMS,

    /** Email envoyé à l'adresse email enregistrée */
    EMAIL,

    /** Message affiché dans l'application (in-app notification) */
    IN_APP;

    /**
     * Méthode utilitaire pour obtenir une description lisible du canal.
     */
    public String getDescription() {
        switch (this) {
            case PUSH:
                return "Notification push via FCM ou APNs.";
            case SMS:
                return "Notification par SMS.";
            case EMAIL:
                return "Notification par email.";
            case IN_APP:
                return "Notification affichée dans l'application.";
            default:
                return "Canal de notification inconnu.";
        }
    }
}