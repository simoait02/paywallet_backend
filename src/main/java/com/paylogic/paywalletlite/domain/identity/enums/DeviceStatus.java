package com.paylogic.paywalletlite.domain.identity.enums;

/**
 * Enum représentant les différents états possibles d'un appareil (device)
 * enregistré par un utilisateur dans le système PayWallet Lite.
 *
 * Un device suit un cycle de vie :
 * ACTIVE → SUSPENDED (temporaire) ou REVOKED (bloqué) ou LOST (perdu/volé)
 */
public enum DeviceStatus {

    /** Appareil actif : l'utilisateur peut se connecter et synchroniser depuis cet appareil */
    ACTIVE,

    /**
     * Appareil suspendu temporairement (exemple : comportement suspect,
     * trop d'échecs de synchronisation). Peut être réactivé.
     */
    SUSPENDED,

    /**
     * Appareil révoqué définitivement (exemple : l'utilisateur retire l'accès,
     * ou l'administrateur bloque l'appareil). Ne peut plus se connecter.
     */
    REVOKED,

    /**
     * Appareil déclaré perdu ou volé par l'utilisateur.
     * Le wallet est automatiquement verrouillé et les tokens offline sont invalidés.
     */
    LOST;

    /**
     * Méthode utilitaire pour obtenir une description lisible du statut.
     */
    public String getDescription() {
        switch (this) {
            case ACTIVE:
                return "L'appareil est actif et autorisé à se synchroniser.";
            case SUSPENDED:
                return "L'appareil est temporairement suspendu pour vérification.";
            case REVOKED:
                return "L'appareil est définitivement révoqué et ne peut plus se connecter.";
            case LOST:
                return "L'appareil est déclaré perdu ou volé par son propriétaire.";
            default:
                return "Statut inconnu.";
        }
    }
}