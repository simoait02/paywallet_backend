package com.paylogic.paywalletlite.domain.wallet;

import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_configs", schema = "pwl_app")
public class WalletConfig {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "config_id", updatable = false, nullable = false)
    private UUID configId;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 20)
    private WalletType walletType;

    @Column(name = "daily_spending_limit", precision = 19, scale = 2)
    private BigDecimal dailySpendingLimit;

    @Column(name = "monthly_spending_limit", precision = 19, scale = 2)
    private BigDecimal monthlySpendingLimit;

    @Column(name = "max_single_transaction", precision = 19, scale = 2)
    private BigDecimal maxSingleTransaction;

    @Column(name = "max_offline_balance", precision = 19, scale = 2)
    private BigDecimal maxOfflineBalance;

    @Column(name = "key_rotation_period_days")
    private Integer keyRotationPeriodDays;

    @Column(name = "requires_biometric_for_offline")
    private Boolean requiresBiometricForOffline;

    @Column(name = "pin_max_attempts")
    private Integer pinMaxAttempts;

    @Column(name = "offline_transaction_timeout_minutes")
    private Integer offlineTransactionTimeoutMinutes;

    @Column(name = "allow_token_transfer")
    private Boolean allowTokenTransfer;

    @Column(name = "allow_merchant_payment")
    private Boolean allowMerchantPayment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WalletConfigStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructeur
    public WalletConfig() {
        this.createdAt = LocalDateTime.now();
        this.status = WalletConfigStatus.ACTIVE;
    }

    // Getters et Setters (générer avec IntelliJ : Alt+Insert)
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}