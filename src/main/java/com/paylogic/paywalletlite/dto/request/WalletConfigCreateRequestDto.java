package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

public class WalletConfigCreateRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Wallet type is required")
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
}