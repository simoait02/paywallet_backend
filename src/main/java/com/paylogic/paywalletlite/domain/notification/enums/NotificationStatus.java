package com.paylogic.paywalletlite.domain.notification.enums;

/**
 * Enum représentant les différents états d'envoi d'une notification.
 *
 * Cycle de vie :
 * PENDING → SENT → DELIVERED → READ
 * PENDING → SENT → FAILED
 */
public enum NotificationStatus {

    /** Notification en attente d'envoi */
    PENDING,

    /** Notification envoyée au fournisseur (FCM, APNs, SMS gateway) */
    SENT,

    /** Notification livrée avec succès au device */
    DELIVERED,

    /** Notification lue par l'utilisateur (si applicable) */
    READ,

    /** Échec de l'envoi de la notification */
    FAILED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case PENDING:
                return "Notification en attente d'envoi.";
            case SENT:
                return "Notification transmise au fournisseur.";
            case DELIVERED:
                return "Notification livrée au device destinataire.";
            case READ:
                return "Notification lue par l'utilisateur.";
            case FAILED:
                return "Échec de l'envoi de la notification.";
            default:
                return "Statut de notification inconnu.";
        }
    }
}