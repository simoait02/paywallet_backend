package com.paylogic.paywalletlite.domain.notification.enums;

/**
 * Enum représentant les différents types d'événements d'audit
 * enregistrés dans le journal d'audit (AuditLog) du système PayWallet Lite.
 *
 * Chaque événement d'audit est immuable et horodaté pour garantir
 * la traçabilité complète de toutes les actions sensibles.
 */
public enum AuditEventType {

    // ============================================================
    // TOKENS
    // ============================================================
    TOKEN_CREATED,
    TOKEN_ALLOCATED,
    TOKEN_TRANSFERRED,
    TOKEN_REDEEMED,
    TOKEN_EXPIRED,
    TOKEN_REVOKED,

    // ============================================================
    // SYNCHRONISATION
    // ============================================================
    SYNC_INITIATED,
    SYNC_COMPLETED,
    SYNC_FAILED,
    SYNC_PARTIAL,

    // ============================================================
    // TRANSACTIONS
    // ============================================================
    TRANSACTION_CREATED,
    TRANSACTION_COMPLETED,
    TRANSACTION_FAILED,
    OVERPAYMENT_DETECTED,
    OVERPAYMENT_REFUNDED,
    OVERPAYMENT_FORFEITED,

    // ============================================================
    // DOUBLE SPENDING & FRAUDE
    // ============================================================
    DOUBLE_SPEND_DETECTED,
    FRAUD_ALERT_TRIGGERED,

    // ============================================================
    // AUTHENTIFICATION
    // ============================================================
    AUTH_SUCCESS,
    AUTH_FAILURE,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,

    // ============================================================
    // WALLET
    // ============================================================
    WALLET_CREATED,
    WALLET_APPROVED,
    WALLET_REJECTED,
    WALLET_CONFIGURED,
    WALLET_ACTIVATED,
    WALLET_LOCKED,
    WALLET_UNLOCKED,
    WALLET_FROZEN,
    WALLET_CLOSED,
    WALLET_FUNDED,
    WALLET_CREDITED,
    WALLET_DEBITED,
    BALANCE_UPDATED,

    // ============================================================
    // CRÉDIT
    // ============================================================
    CREDIT_LINE_CREATED,
    CREDIT_REPAYMENT_MADE,
    CREDIT_REPAYMENT_OVERDUE,
    CRECIT_DEBT_RECORDED,

    // ============================================================
    // SÉCURITÉ
    // ============================================================
    KEY_ROTATION,
    KEY_COMPROMISED,
    DEVICE_BOUND,
    DEVICE_REVOKED,

    // ============================================================
    // ADMINISTRATION
    // ============================================================
    CONFIG_UPDATED,
    MANUAL_INTERVENTION;

    /**
     * Retourne la catégorie de l'événement pour le filtrage.
     */
    public String getCategory() {
        switch (this) {
            case TOKEN_CREATED:
            case TOKEN_ALLOCATED:
            case TOKEN_TRANSFERRED:
            case TOKEN_REDEEMED:
            case TOKEN_EXPIRED:
            case TOKEN_REVOKED:
                return "TOKEN";
            case SYNC_INITIATED:
            case SYNC_COMPLETED:
            case SYNC_FAILED:
            case SYNC_PARTIAL:
                return "SYNCHRONIZATION";
            case TRANSACTION_CREATED:
            case TRANSACTION_COMPLETED:
            case TRANSACTION_FAILED:
            case OVERPAYMENT_DETECTED:
            case OVERPAYMENT_REFUNDED:
            case OVERPAYMENT_FORFEITED:
                return "TRANSACTION";
            case DOUBLE_SPEND_DETECTED:
            case FRAUD_ALERT_TRIGGERED:
                return "FRAUD";
            case AUTH_SUCCESS:
            case AUTH_FAILURE:
            case ACCOUNT_LOCKED:
            case ACCOUNT_UNLOCKED:
                return "AUTHENTICATION";
            case WALLET_CREATED:
            case WALLET_APPROVED:
            case WALLET_REJECTED:
            case WALLET_CONFIGURED:
            case WALLET_ACTIVATED:
            case WALLET_LOCKED:
            case WALLET_UNLOCKED:
            case WALLET_FROZEN:
            case WALLET_CLOSED:
            case WALLET_FUNDED:
            case WALLET_CREDITED:
            case WALLET_DEBITED:
            case BALANCE_UPDATED:
                return "WALLET";
            case CREDIT_LINE_CREATED:
            case CREDIT_REPAYMENT_MADE:
            case CREDIT_REPAYMENT_OVERDUE:
                return "CREDIT";
            case KEY_ROTATION:
            case KEY_COMPROMISED:
            case DEVICE_BOUND:
            case DEVICE_REVOKED:
                return "SECURITY";
            case CONFIG_UPDATED:
            case MANUAL_INTERVENTION:
                return "ADMINISTRATION";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Retourne la sévérité de l'événement.
     */
    public String getSeverity() {
        switch (this) {
            case DOUBLE_SPEND_DETECTED:
            case FRAUD_ALERT_TRIGGERED:
            case KEY_COMPROMISED:
                return "CRITICAL";
            case AUTH_FAILURE:
            case ACCOUNT_LOCKED:
            case WALLET_FROZEN:
            case CREDIT_REPAYMENT_OVERDUE:
                return "HIGH";
            case SYNC_FAILED:
            case TRANSACTION_FAILED:
            case TOKEN_REVOKED:
                return "MEDIUM";
            default:
                return "LOW";
        }
    }

    public String getDescription() {
        switch (this) {
            case TOKEN_CREATED:
                return "Nouveau token offline créé par le backend.";
            case TOKEN_ALLOCATED:
                return "Token alloué à un wallet pour usage offline.";
            case TOKEN_TRANSFERRED:
                return "Token transféré d'un wallet à un autre (mode offline).";
            case TOKEN_REDEEMED:
                return "Token racheté et crédité au wallet destinataire.";
            case TOKEN_EXPIRED:
                return "Token expiré avant rachat.";
            case TOKEN_REVOKED:
                return "Token révoqué par l'administrateur.";
            case SYNC_INITIATED:
                return "Session de synchronisation démarrée.";
            case SYNC_COMPLETED:
                return "Session de synchronisation terminée avec succès.";
            case SYNC_FAILED:
                return "Session de synchronisation échouée.";
            case SYNC_PARTIAL:
                return "Session de synchronisation partiellement réussie.";
            case TRANSACTION_CREATED:
                return "Nouvelle transaction créée.";
            case TRANSACTION_COMPLETED:
                return "Transaction finalisée avec succès.";
            case TRANSACTION_FAILED:
                return "Transaction échouée.";
            case OVERPAYMENT_DETECTED:
                return "Surpaiement détecté lors de la synchronisation.";
            case OVERPAYMENT_REFUNDED:
                return "Surpaiement remboursé au payeur.";
            case OVERPAYMENT_FORFEITED:
                return "Surpaiement abandonné après délai.";
            case DOUBLE_SPEND_DETECTED:
                return "Tentative de double dépense détectée.";
            case FRAUD_ALERT_TRIGGERED:
                return "Alerte de fraude déclenchée.";
            case AUTH_SUCCESS:
                return "Authentification réussie.";
            case AUTH_FAILURE:
                return "Échec d'authentification.";
            case ACCOUNT_LOCKED:
                return "Compte verrouillé.";
            case ACCOUNT_UNLOCKED:
                return "Compte déverrouillé.";
            case WALLET_CREATED:
                return "Wallet créé.";
            case WALLET_APPROVED:
                return "Wallet approuvé.";
            case WALLET_REJECTED:
                return "Wallet rejeté.";
            case WALLET_CONFIGURED:
                return "Wallet configuré.";
            case WALLET_ACTIVATED:
                return "Wallet activé.";
            case WALLET_LOCKED:
                return "Wallet verrouillé.";
            case WALLET_UNLOCKED:
                return "Wallet déverrouillé.";
            case WALLET_FROZEN:
                return "Wallet gelé.";
            case WALLET_CLOSED:
                return "Wallet fermé définitivement.";
            case BALANCE_UPDATED:
                return "Solde du wallet mis à jour.";
            case CREDIT_LINE_CREATED:
                return "Ligne de crédit créée.";
            case CREDIT_REPAYMENT_MADE:
                return "Remboursement de crédit effectué.";
            case CREDIT_REPAYMENT_OVERDUE:
                return "Remboursement de crédit en retard.";
            case KEY_ROTATION:
                return "Rotation de clé cryptographique effectuée.";
            case KEY_COMPROMISED:
                return "Clé compromise détectée.";
            case DEVICE_BOUND:
                return "Nouveau device associé au compte.";
            case DEVICE_REVOKED:
                return "Device révoqué.";
            case CONFIG_UPDATED:
                return "Configuration système mise à jour.";
            case MANUAL_INTERVENTION:
                return "Intervention manuelle d'un administrateur.";
            default:
                return "Type d'événement inconnu.";
        }
    }
}