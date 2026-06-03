package com.paylogic.paywalletlite.mapper;

import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.dto.response.WalletConfigResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsable de la conversion entre l'entité WalletConfig
 * et son DTO de réponse WalletConfigResponseDto.
 *
 * Ce mapper est utilisé par les contrôleurs pour transformer
 * les entités JPA en objets sérialisables pour l'API REST.
 */
@Component
public class WalletConfigMapper {

    /**
     * Convertit une entité WalletConfig en WalletConfigResponseDto.
     *
     * @param config L'entité WalletConfig à convertir
     * @return Le DTO correspondant
     */
    public WalletConfigResponseDto toDto(WalletConfig config) {
        if (config == null) {
            return null;
        }

        WalletConfigResponseDto dto = new WalletConfigResponseDto();
        dto.setConfigId(config.getConfigId());
        dto.setWalletType(config.getWalletType());
        dto.setDailySpendingLimit(config.getDailySpendingLimit());
        dto.setMonthlySpendingLimit(config.getMonthlySpendingLimit());
        dto.setMaxSingleTransaction(config.getMaxSingleTransaction());
        dto.setMaxOfflineBalance(config.getMaxOfflineBalance());
        dto.setKeyRotationPeriodDays(config.getKeyRotationPeriodDays());
        dto.setRequiresBiometricForOffline(config.getRequiresBiometricForOffline());
        dto.setPinMaxAttempts(config.getPinMaxAttempts());
        dto.setOfflineTransactionTimeoutMinutes(config.getOfflineTransactionTimeoutMinutes());
        dto.setAllowTokenTransfer(config.getAllowTokenTransfer());
        dto.setAllowMerchantPayment(config.getAllowMerchantPayment());
        dto.setStatus(config.getStatus());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setUpdatedAt(config.getUpdatedAt());

        return dto;
    }

    /**
     * Convertit une liste d'entités WalletConfig en liste de WalletConfigResponseDto.
     *
     * @param configs Liste des entités à convertir
     * @return Liste des DTOs correspondants
     */
    public List<WalletConfigResponseDto> toDtoList(List<WalletConfig> configs) {
        if (configs == null) {
            return null;
        }

        return configs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}