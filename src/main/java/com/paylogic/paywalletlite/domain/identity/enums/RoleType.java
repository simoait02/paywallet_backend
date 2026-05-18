package com.paylogic.paywalletlite.domain.identity.enums;

/**
 * Enum représentant les différents rôles attribuables à un utilisateur
 * dans le système PayWallet Lite.
 *
 * Chaque rôle détermine les permissions et les fonctionnalités accessibles.
 */
public enum RoleType {

    /**
     * Administrateur système : accès complet au back-office, gestion des utilisateurs,
     * configuration du système, consultation des audits et résolution des litiges.
     */
    ADMIN,

    /**
     * Client standard : peut créer un wallet, effectuer des paiements P2P et marchands,
     * allouer des tokens offline, synchroniser ses transactions.
     */
    CUSTOMER,

    /**
     * Marchand : peut recevoir des paiements, convertir les tokens reçus en solde,
     * consulter son historique de transactions et générer des rapports simples.
     */
    MERCHANT;

    /**
     * Méthode utilitaire pour obtenir une description lisible du rôle.
     */
    public String getDescription() {
        switch (this) {
            case ADMIN:
                return "Administrateur avec accès complet au système.";
            case CUSTOMER:
                return "Client standard effectuant des paiements P2P et marchands.";
            case MERCHANT:
                return "Marchand recevant des paiements pour des biens ou services.";
            default:
                return "Rôle inconnu.";
        }
    }
}