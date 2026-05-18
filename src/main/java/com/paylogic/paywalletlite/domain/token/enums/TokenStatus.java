package com.paylogic.paywalletlite.domain.token.enums;

/**
 * Enum représentant les différents états possibles d'un token de paiement offline
 * dans le système PayWallet Lite.
 *
 * Cycle de vie complet :
 * CREATED → ALLOCATED → TRANSFERRED (0 à N fois) → REDEEMED
 * CREATED → ALLOCATED → EXPIRED (si non utilisé)
 * CREATED/ALLOCATED/TRANSFERRED → REVOKED (anomalie)
 * Tout état → INVALID (corruption ou falsification détectée)
 */
public enum TokenStatus {

    /** Token créé par le backend mais pas encore transmis au device */
    CREATED,

    /** Token alloué et stocké sur le device du propriétaire, prêt à être utilisé */
    ALLOCATED,

    /** Token transféré d'un wallet à un autre (phase offline) */
    TRANSFERRED,

    /** Token racheté (redeemed) auprès du backend, valeur créditée au dernier porteur */
    REDEEMED,

    /** Token expiré avant d'avoir été racheté, valeur retournée à l'émetteur */
    EXPIRED,

    /** Token invalidé suite à une détection de fraude ou corruption de données */
    INVALID,

    /** Token révoqué manuellement par l'administrateur */
    REVOKED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case CREATED:
                return "Le token a été créé par le backend mais pas encore livré au device.";
            case ALLOCATED:
                return "Le token est alloué au wallet propriétaire et utilisable.";
            case TRANSFERRED:
                return "Le token a été transféré à un autre wallet en mode offline.";
            case REDEEMED:
                return "Le token a été racheté et crédité au dernier porteur.";
            case EXPIRED:
                return "Le token a expiré avant rachat, valeur retournée à l'émetteur.";
            case INVALID:
                return "Le token a été invalidé pour cause de fraude ou corruption.";
            case REVOKED:
                return "Le token a été révoqué manuellement par l'administrateur.";
            default:
                return "Statut inconnu.";
        }
    }
}