package com.paylogic.paywalletlite.repository.wallet;

import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletConfigRepository {
    WalletConfig save(WalletConfig config);
    Optional<WalletConfig> findById(UUID configId);
    /**
     * Récupère toutes les configurations ayant un statut donné.
     */
    List<WalletConfig> findByStatus(WalletConfigStatus status);

    List<WalletConfig> findAll();

    /**
     * Récupère toutes les configurations pour un type de wallet donné.
     *
     * @param walletType Type de wallet (GOLD, SILVER, BASIC)
     * @return Liste des WalletConfig correspondantes
     */
    List<WalletConfig> findByWalletType(WalletType walletType);

    /**
     * Récupère toutes les configurations actives pour un type de wallet donné.
     *
     * @param walletType Type de wallet (GOLD, SILVER, BASIC)
     * @return Liste des WalletConfig actives
     */
    List<WalletConfig> findByWalletTypeAndStatus(WalletType walletType, WalletConfigStatus status);
}