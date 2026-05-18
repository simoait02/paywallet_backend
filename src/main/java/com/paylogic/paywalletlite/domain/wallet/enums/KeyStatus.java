package com.paylogic.paywalletlite.domain.wallet.enums;

/**
 * Enum représentant les différents états possibles
 * d'une paire de clés cryptographiques (WalletKeyPair).
 *
 * Cycle de vie d'une clé :
 * ACTIVE → PENDING_ROTATION → EXPIRED
 * ACTIVE → REVOKED ou COMPROMISED (anomalie)
 */
public enum KeyStatus {

    /** Clé active et utilisable pour signer des transactions */
    ACTIVE,

    /** Clé dont la période de validité est dépassée */
    EXPIRED,

    /** Clé révoquée volontairement par l'administrateur ou l'utilisateur */
    REVOKED,

    /** Clé compromise (volée, exposée) nécessitant une rotation immédiate */
    COMPROMISED,

    /**
     * Clé en attente de rotation : une nouvelle clé est en cours de génération,
     * l'ancienne clé reste temporairement active pour vérifier les signatures existantes.
     */
    PENDING_ROTATION;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "La clé est active et utilisable pour les signatures.";
            case EXPIRED:
                return "La clé a expiré et n'est plus valide pour les nouvelles signatures.";
            case REVOKED:
                return "La clé a été révoquée volontairement par l'autorité compétente.";
            case COMPROMISED:
                return "La clé est compromise et doit être remplacée immédiatement.";
            case PENDING_ROTATION:
                return "La clé est en attente de rotation vers une nouvelle clé.";
            default:
                return "Statut inconnu.";
        }
    }
}