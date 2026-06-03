package com.paylogic.paywalletlite.controller.admin;

import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import com.paylogic.paywalletlite.dto.request.WalletConfigCreateRequestDto;
import com.paylogic.paywalletlite.dto.request.WalletConfigUpdateRequestDto;
import com.paylogic.paywalletlite.dto.response.WalletConfigResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.wallet.WalletConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/wallet-configs")
public class AdminWalletConfigController {

    private final WalletConfigService walletConfigService;

    @Autowired
    public AdminWalletConfigController(WalletConfigService walletConfigService) {
        this.walletConfigService = walletConfigService;
    }

    @PostMapping
    public ResponseEntity<WalletConfigResponseDto> createConfig(
            @Valid @RequestBody WalletConfigCreateRequestDto dto) {
        WalletConfig config = walletConfigService.createConfig(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(config));
    }

    @GetMapping("/{configId}")
    public ResponseEntity<WalletConfigResponseDto> getConfig(@PathVariable UUID configId) {
        WalletConfig config = walletConfigService.getConfigById(configId)
                .orElseThrow(() -> new BusinessException("Config not found: " + configId));
        return ResponseEntity.ok(toDto(config));
    }

    @PutMapping("/{configId}")
    public ResponseEntity<WalletConfigResponseDto> updateConfig(
            @PathVariable UUID configId,
            @Valid @RequestBody WalletConfigUpdateRequestDto dto) {
        WalletConfig updated = walletConfigService.updateConfig(configId, dto);
        return ResponseEntity.ok(toDto(updated));
    }

    @PatchMapping("/{configId}/deprecate")
    public ResponseEntity<Void> deprecateConfig(@PathVariable UUID configId) {
        walletConfigService.deprecateConfig(configId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /admin/wallet-configs
     *
     * Récupère toutes les configurations de wallet.
     * Filtre optionnel par statut.
     *
     * @param status Filtre optionnel : ACTIVE ou DEPRECATED
     * @return Liste de WalletConfigResponseDto
     */
    @GetMapping
    public ResponseEntity<List<WalletConfigResponseDto>> getAllConfigs(
            @RequestParam(value = "status", required = false) String status) {

        WalletConfigStatus configStatus = null;
        if (status != null) {
            try {
                configStatus = WalletConfigStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid status filter: " + status
                        + ". Allowed values: ACTIVE, DEPRECATED");
            }
        }

        List<WalletConfig> configs = walletConfigService.getAllConfigs(configStatus);

        List<WalletConfigResponseDto> response = configs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /admin/wallet-configs/type/{walletType}
     *
     * Récupère toutes les configurations pour un type de wallet donné.
     *
     * @param walletType Type de wallet : GOLD, SILVER, BASIC
     * @return Liste de WalletConfigResponseDto
     */
    @GetMapping("/type/{walletType}")
    public ResponseEntity<List<WalletConfigResponseDto>> getConfigsByWalletType(
            @PathVariable String walletType) {

        WalletType type;
        try {
            type = WalletType.valueOf(walletType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid wallet type: " + walletType
                    + ". Allowed values: GOLD, SILVER, BASIC");
        }

        List<WalletConfig> configs = walletConfigService.getConfigsByWalletType(type);
        List<WalletConfigResponseDto> response = configs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /admin/wallet-configs/type/{walletType}/active
     *
     * Récupère uniquement les configurations actives pour un type de wallet donné.
     *
     * @param walletType Type de wallet : GOLD, SILVER, BASIC
     * @return Liste de WalletConfigResponseDto actives
     */
    @GetMapping("/type/{walletType}/active")
    public ResponseEntity<List<WalletConfigResponseDto>> getActiveConfigsByWalletType(
            @PathVariable String walletType) {

        WalletType type;
        try {
            type = WalletType.valueOf(walletType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid wallet type: " + walletType
                    + ". Allowed values: GOLD, SILVER, BASIC");
        }

        List<WalletConfig> configs = walletConfigService.getActiveConfigsByWalletType(type);
        List<WalletConfigResponseDto> response = configs.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    private WalletConfigResponseDto toDto(WalletConfig config) {
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
        return dto;
    }
}