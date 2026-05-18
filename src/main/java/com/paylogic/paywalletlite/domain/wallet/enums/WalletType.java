package com.paylogic.paywalletlite.domain.wallet.enums;

/**
 * Enum représentant les différents types de portefeuilles (wallets)
 * disponibles dans le système PayWallet Lite.
 *
 * Chaque type détermine les limites de transaction, les frais applicables,
 * et les fonctionnalités accessibles (crédit offline, nombre de devices, etc.).
 */
public enum WalletType {

    /**
     * Portefeuille GOLD : niveau le plus élevé.
     * - Limites de transaction élevées
     * - Crédit offline disponible (jusqu'à 50 000 XAF)
     * - Jusqu'à 5 devices autorisés
     * - Support prioritaire
     */
    GOLD,

    /**
     * Portefeuille SILVER : niveau intermédiaire.
     * - Limites de transaction modérées
     * - Crédit offline limité (jusqu'à 20 000 XAF)
     * - Jusqu'à 3 devices autorisés
     */
    SILVER,

    /**
     * Portefeuille BASIC : niveau d'entrée.
     * - Limites de transaction basses
     * - Pas de crédit offline
     * - 1 device autorisé
     * - Vérification KYC obligatoire pour débloquer les limites supérieures
     */
    BASIC;

    /**
     * Méthode utilitaire pour obtenir une description lisible du type de wallet.
     */
    public String getDescription() {
        switch (this) {
            case GOLD:
                return "Portefeuille premium avec les limites les plus élevées et toutes les fonctionnalités.";
            case SILVER:
                return "Portefeuille intermédiaire avec fonctionnalités avancées.";
            case BASIC:
                return "Portefeuille de base avec fonctionnalités essentielles.";
            default:
                return "Type inconnu.";
        }
    }
}