package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletConfigResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID configId;
    private WalletType walletType;
    private BigDecimal dailySpendingLimit;
    private BigDecimal monthlySpendingLimit;
    private BigDecimal maxSingleTransaction;
    private BigDecimal maxOfflineBalance;
    private Integer keyRotationPeriodDays;
    private Boolean requiresBiometricForOffline;
    private Integer pinMaxAttempts;
    private Integer offlineTransactionTimeoutMinutes;
    private Boolean allowTokenTransfer;
    private Boolean allowMerchantPayment;
    private WalletConfigStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    

    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public WalletType getWalletType() { return walletType; }
    public void setWalletType(WalletType walletType) { this.walletType = walletType; }

    public BigDecimal getDailySpendingLimit() { return dailySpendingLimit; }
    public void setDailySpendingLimit(BigDecimal dailySpendingLimit) { this.dailySpendingLimit = dailySpendingLimit; }

    public BigDecimal getMonthlySpendingLimit() { return monthlySpendingLimit; }
    public void setMonthlySpendingLimit(BigDecimal monthlySpendingLimit) { this.monthlySpendingLimit = monthlySpendingLimit; }

    public BigDecimal getMaxSingleTransaction() { return maxSingleTransaction; }
    public void setMaxSingleTransaction(BigDecimal maxSingleTransaction) { this.maxSingleTransaction = maxSingleTransaction; }

    public BigDecimal getMaxOfflineBalance() { return maxOfflineBalance; }
    public void setMaxOfflineBalance(BigDecimal maxOfflineBalance) { this.maxOfflineBalance = maxOfflineBalance; }

    public Integer getKeyRotationPeriodDays() { return keyRotationPeriodDays; }
    public void setKeyRotationPeriodDays(Integer keyRotationPeriodDays) { this.keyRotationPeriodDays = keyRotationPeriodDays; }

    public Boolean getRequiresBiometricForOffline() { return requiresBiometricForOffline; }
    public void setRequiresBiometricForOffline(Boolean requiresBiometricForOffline) { this.requiresBiometricForOffline = requiresBiometricForOffline; }

    public Integer getPinMaxAttempts() { return pinMaxAttempts; }
    public void setPinMaxAttempts(Integer pinMaxAttempts) { this.pinMaxAttempts = pinMaxAttempts; }

    public Integer getOfflineTransactionTimeoutMinutes() { return offlineTransactionTimeoutMinutes; }
    public void setOfflineTransactionTimeoutMinutes(Integer offlineTransactionTimeoutMinutes) { this.offlineTransactionTimeoutMinutes = offlineTransactionTimeoutMinutes; }

    public Boolean getAllowTokenTransfer() { return allowTokenTransfer; }
    public void setAllowTokenTransfer(Boolean allowTokenTransfer) { this.allowTokenTransfer = allowTokenTransfer; }

    public Boolean getAllowMerchantPayment() { return allowMerchantPayment; }
    public void setAllowMerchantPayment(Boolean allowMerchantPayment) { this.allowMerchantPayment = allowMerchantPayment; }

    public WalletConfigStatus getStatus() { return status; }
    public void setStatus(WalletConfigStatus status) { this.status = status; }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}