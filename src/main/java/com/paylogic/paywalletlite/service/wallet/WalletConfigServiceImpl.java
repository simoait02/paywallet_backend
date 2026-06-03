package com.paylogic.paywalletlite.service.wallet;

import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import com.paylogic.paywalletlite.dto.request.WalletConfigCreateRequestDto;
import com.paylogic.paywalletlite.dto.request.WalletConfigUpdateRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.wallet.WalletConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class WalletConfigServiceImpl implements WalletConfigService {

    private final WalletConfigRepository walletConfigRepository;

    @Autowired
    public WalletConfigServiceImpl(WalletConfigRepository walletConfigRepository) {
        this.walletConfigRepository = walletConfigRepository;
    }

    @Override
    public WalletConfig createConfig(WalletConfigCreateRequestDto dto) {
        WalletConfig config = new WalletConfig();
        config.setWalletType(dto.getWalletType());
        config.setDailySpendingLimit(dto.getDailySpendingLimit() != null ? dto.getDailySpendingLimit() : new BigDecimal("50000.00"));
        config.setMonthlySpendingLimit(dto.getMonthlySpendingLimit() != null ? dto.getMonthlySpendingLimit() : new BigDecimal("500000.00"));
        config.setMaxSingleTransaction(dto.getMaxSingleTransaction() != null ? dto.getMaxSingleTransaction() : new BigDecimal("10000.00"));
        config.setMaxOfflineBalance(dto.getMaxOfflineBalance() != null ? dto.getMaxOfflineBalance() : new BigDecimal("5000.00"));
        config.setKeyRotationPeriodDays(dto.getKeyRotationPeriodDays() != null ? dto.getKeyRotationPeriodDays() : 30);
        config.setRequiresBiometricForOffline(dto.getRequiresBiometricForOffline() != null ? dto.getRequiresBiometricForOffline() : false);
        config.setPinMaxAttempts(dto.getPinMaxAttempts() != null ? dto.getPinMaxAttempts() : 3);
        config.setOfflineTransactionTimeoutMinutes(dto.getOfflineTransactionTimeoutMinutes() != null ? dto.getOfflineTransactionTimeoutMinutes() : 5);
        config.setAllowTokenTransfer(dto.getAllowTokenTransfer() != null ? dto.getAllowTokenTransfer() : true);
        config.setAllowMerchantPayment(dto.getAllowMerchantPayment() != null ? dto.getAllowMerchantPayment() : true);
        config.setStatus(WalletConfigStatus.ACTIVE);
        config.setCreatedAt(LocalDateTime.now());

        return walletConfigRepository.save(config);
    }

    @Override
    public WalletConfig updateConfig(UUID configId, WalletConfigUpdateRequestDto dto) {
        WalletConfig config = walletConfigRepository.findById(configId)
                .orElseThrow(() -> new BusinessException("Config not found: " + configId));

        if (dto.getDailySpendingLimit() != null) config.setDailySpendingLimit(dto.getDailySpendingLimit());
        if (dto.getMonthlySpendingLimit() != null) config.setMonthlySpendingLimit(dto.getMonthlySpendingLimit());
        if (dto.getMaxSingleTransaction() != null) config.setMaxSingleTransaction(dto.getMaxSingleTransaction());
        if (dto.getMaxOfflineBalance() != null) config.setMaxOfflineBalance(dto.getMaxOfflineBalance());
        if (dto.getKeyRotationPeriodDays() != null) config.setKeyRotationPeriodDays(dto.getKeyRotationPeriodDays());
        if (dto.getRequiresBiometricForOffline() != null) config.setRequiresBiometricForOffline(dto.getRequiresBiometricForOffline());
        if (dto.getPinMaxAttempts() != null) config.setPinMaxAttempts(dto.getPinMaxAttempts());
        if (dto.getOfflineTransactionTimeoutMinutes() != null) config.setOfflineTransactionTimeoutMinutes(dto.getOfflineTransactionTimeoutMinutes());
        if (dto.getAllowTokenTransfer() != null) config.setAllowTokenTransfer(dto.getAllowTokenTransfer());
        if (dto.getAllowMerchantPayment() != null) config.setAllowMerchantPayment(dto.getAllowMerchantPayment());
        config.setUpdatedAt(LocalDateTime.now());

        return walletConfigRepository.save(config);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletConfig> getConfigById(UUID configId) {
        return walletConfigRepository.findById(configId);
    }

    @Override
    public List<WalletConfig> findByStatus(WalletConfigStatus status) {
        return Collections.emptyList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> getAllConfigs(WalletConfigStatus status) {
        if (status != null) {
            return walletConfigRepository.findByStatus(status);
        }
        return walletConfigRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> getConfigsByWalletType(WalletType walletType) {
        return walletConfigRepository.findByWalletType(walletType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> getActiveConfigsByWalletType(WalletType walletType) {
        return walletConfigRepository.findByWalletTypeAndStatus(walletType, WalletConfigStatus.ACTIVE);
    }

    @Override
    public void deprecateConfig(UUID configId) {
        WalletConfig config = walletConfigRepository.findById(configId)
                .orElseThrow(() -> new BusinessException("Config not found: " + configId));
        config.setStatus(WalletConfigStatus.DEPRECATED);
        config.setUpdatedAt(LocalDateTime.now());
        walletConfigRepository.save(config);
    }
}