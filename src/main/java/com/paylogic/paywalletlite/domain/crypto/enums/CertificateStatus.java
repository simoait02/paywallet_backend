package com.paylogic.paywalletlite.domain.crypto.enums;

/**
 * Enum représentant les différents états possibles
 * d'un certificat numérique X.509 dans le système PayWallet Lite.
 *
 * Cycle de vie :
 * PENDING (optionnel) → VALID → EXPIRED
 * VALID → REVOKED (révocation anticipée)
 */
public enum CertificateStatus {

    /** Certificat valide et utilisable pour la vérification de signatures */
    VALID,

    /** Certificat dont la période de validité est dépassée */
    EXPIRED,

    /** Certificat révoqué avant sa date d'expiration normale */
    REVOKED,

    /** Certificat en attente de validation ou d'activation */
    PENDING;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case VALID:
                return "Le certificat est valide et peut être utilisé.";
            case EXPIRED:
                return "Le certificat a expiré et n'est plus valide.";
            case REVOKED:
                return "Le certificat a été révoqué avant son expiration.";
            case PENDING:
                return "Le certificat est en attente de validation.";
            default:
                return "Statut inconnu.";
        }
    }
}