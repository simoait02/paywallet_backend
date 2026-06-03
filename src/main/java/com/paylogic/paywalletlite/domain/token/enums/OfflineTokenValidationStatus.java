package com.paylogic.paywalletlite.domain.token.enums;

/**
 * Statuts de validation d'un token offline lors de la synchronisation.
 */
public enum OfflineTokenValidationStatus {

    /** En attente de validation */
    PENDING_VALIDATION,

    /** Token valide : signature backend OK, chaîne de transfert OK, non expiré */
    VALID,

    /** Token rejeté : signature backend invalide */
    INVALID_BACKEND_SIGNATURE,

    /** Token rejeté : chaîne de transfert corrompue */
    INVALID_TRANSFER_CHAIN,

    /** Token rejeté : détenteur final différent du receiver */
    HOLDER_MISMATCH,

    /** Token rejeté : expiré */
    EXPIRED,

    /** Token rejeté : déjà racheté (double dépense) */
    ALREADY_REDEEMED,

    /** Token racheté avec succès */
    REDEEMED
}