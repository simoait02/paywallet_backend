package com.paylogic.paywalletlite.service.wallet;

import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import com.paylogic.paywalletlite.dto.request.WalletConfigCreateRequestDto;
import com.paylogic.paywalletlite.dto.request.WalletConfigUpdateRequestDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletConfigService {
    WalletConfig createConfig(WalletConfigCreateRequestDto dto);
    WalletConfig updateConfig(UUID configId, WalletConfigUpdateRequestDto dto);
    Optional<WalletConfig> getConfigById(UUID configId);
    /**
     * Récupère toutes les configurations ayant un statut donné.
     */
    List<WalletConfig> findByStatus(WalletConfigStatus status);

    @Transactional(readOnly = true)
    List<WalletConfig> getAllConfigs(WalletConfigStatus status);

    /**
     * Récupère toutes les configurations pour un type de wallet donné.
     */
    List<WalletConfig> getConfigsByWalletType(WalletType walletType);

    /**
     * Récupère toutes les configurations actives pour un type de wallet donné.
     */
    List<WalletConfig> getActiveConfigsByWalletType(WalletType walletType);

    void deprecateConfig(UUID configId);
}