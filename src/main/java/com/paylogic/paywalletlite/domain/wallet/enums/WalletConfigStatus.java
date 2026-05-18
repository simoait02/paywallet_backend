package com.paylogic.paywalletlite.domain.wallet.enums;

/**
 * Enum représentant les différents états possibles
 * d'une configuration de portefeuille (WalletConfig).
 *
 * Les configurations sont versionnées : quand une nouvelle configuration
 * est créée, l'ancienne passe en statut DEPRECATED.
 */
public enum WalletConfigStatus {

    /** Configuration active et appliquée aux wallets concernés */
    ACTIVE,

    /**
     * Configuration dépréciée : remplacée par une version plus récente.
     * Conservée pour l'historique et l'audit.
     */
    DEPRECATED;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "La configuration est active et appliquée aux wallets.";
            case DEPRECATED:
                return "La configuration est obsolète et conservée pour l'historique.";
            default:
                return "Statut inconnu.";
        }
    }
}