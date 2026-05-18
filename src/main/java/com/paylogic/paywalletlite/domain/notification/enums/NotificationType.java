package com.paylogic.paywalletlite.domain.notification.enums;

/**
 * Enum représentant les différents types de notifications
 * envoyées aux utilisateurs du système PayWallet Lite.
 */
public enum NotificationType {

    /** Confirmation de transaction (paiement envoyé ou reçu) */
    TRANSACTION_CONFIRM,

    /** Alerte de token expiré avant utilisation */
    TOKEN_EXPIRED,

    /** Synchronisation terminée avec succès */
    SYNC_COMPLETE,

    /** Rappel d'échéance de remboursement de crédit */
    CREDIT_DUE,

    /** Alerte de sécurité (connexion suspecte, device inconnu) */
    SECURITY_ALERT,

    /** Alerte de solde faible (sous un seuil configurable) */
    BALANCE_LOW,

    /** Rappel de vérification KYC incomplète */
    KYC_REQUIRED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type de notification.
     */
    public String getDescription() {
        switch (this) {
            case TRANSACTION_CONFIRM:
                return "Confirmation d'une transaction réussie.";
            case TOKEN_EXPIRED:
                return "Alerte : un token offline a expiré.";
            case SYNC_COMPLETE:
                return "Synchronisation des transactions terminée.";
            case CREDIT_DUE:
                return "Rappel : échéance de remboursement de crédit.";
            case SECURITY_ALERT:
                return "Alerte de sécurité sur votre compte.";
            case BALANCE_LOW:
                return "Votre solde est bas.";
            case KYC_REQUIRED:
                return "Vérification d'identité requise.";
            default:
                return "Type de notification inconnu.";
        }
    }
}