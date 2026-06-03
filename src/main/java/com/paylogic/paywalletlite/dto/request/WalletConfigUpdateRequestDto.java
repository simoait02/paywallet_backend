package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO pour la configuration d'un wallet.
 *
 * Deux modes d'utilisation :
 * 1. Référencer une config existante via existingConfigId
 * 2. Créer une nouvelle config en remplissant les champs de configuration
 *
 * Priorité : Si existingConfigId est fourni, les autres champs sont ignorés.
 */
public class WalletConfigUpdateRequestDto {

    /**
     * Option 1 : ID d'une configuration existante à réutiliser.
     * Si fourni, tous les autres champs de configuration sont ignorés.
     */
    private UUID existingConfigId;

    /**
     * Option 2 : Création d'une nouvelle configuration.
     * Les champs suivants sont requis UNIQUEMENT si existingConfigId est null.
     */
    private WalletType walletType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Daily spending limit must be positive")
    private BigDecimal dailySpendingLimit;

    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly spending limit must be positive")
    private BigDecimal monthlySpendingLimit;

    @DecimalMin(value = "0.0", inclusive = false, message = "Max single transaction must be positive")
    private BigDecimal maxSingleTransaction;

    @DecimalMin(value = "0.0", message = "Max offline balance must be zero or positive")
    private BigDecimal maxOfflineBalance;

    private Integer keyRotationPeriodDays;

    private Boolean requiresBiometricForOffline;

    private Integer pinMaxAttempts;

    private Integer offlineTransactionTimeoutMinutes;

    private Boolean allowTokenTransfer;

    private Boolean allowMerchantPayment;

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getExistingConfigId() {
        return existingConfigId;
    }

    public void setExistingConfigId(UUID existingConfigId) {
        this.existingConfigId = existingConfigId;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public BigDecimal getDailySpendingLimit() {
        return dailySpendingLimit;
    }

    public void setDailySpendingLimit(BigDecimal dailySpendingLimit) {
        this.dailySpendingLimit = dailySpendingLimit;
    }

    public BigDecimal getMonthlySpendingLimit() {
        return monthlySpendingLimit;
    }

    public void setMonthlySpendingLimit(BigDecimal monthlySpendingLimit) {
        this.monthlySpendingLimit = monthlySpendingLimit;
    }

    public BigDecimal getMaxSingleTransaction() {
        return maxSingleTransaction;
    }

    public void setMaxSingleTransaction(BigDecimal maxSingleTransaction) {
        this.maxSingleTransaction = maxSingleTransaction;
    }

    public BigDecimal getMaxOfflineBalance() {
        return maxOfflineBalance;
    }

    public void setMaxOfflineBalance(BigDecimal maxOfflineBalance) {
        this.maxOfflineBalance = maxOfflineBalance;
    }

    public Integer getKeyRotationPeriodDays() {
        return keyRotationPeriodDays;
    }

    public void setKeyRotationPeriodDays(Integer keyRotationPeriodDays) {
        this.keyRotationPeriodDays = keyRotationPeriodDays;
    }

    public Boolean getRequiresBiometricForOffline() {
        return requiresBiometricForOffline;
    }

    public void setRequiresBiometricForOffline(Boolean requiresBiometricForOffline) {
        this.requiresBiometricForOffline = requiresBiometricForOffline;
    }

    public Integer getPinMaxAttempts() {
        return pinMaxAttempts;
    }

    public void setPinMaxAttempts(Integer pinMaxAttempts) {
        this.pinMaxAttempts = pinMaxAttempts;
    }

    public Integer getOfflineTransactionTimeoutMinutes() {
        return offlineTransactionTimeoutMinutes;
    }

    public void setOfflineTransactionTimeoutMinutes(Integer offlineTransactionTimeoutMinutes) {
        this.offlineTransactionTimeoutMinutes = offlineTransactionTimeoutMinutes;
    }

    public Boolean getAllowTokenTransfer() {
        return allowTokenTransfer;
    }

    public void setAllowTokenTransfer(Boolean allowTokenTransfer) {
        this.allowTokenTransfer = allowTokenTransfer;
    }

    public Boolean getAllowMerchantPayment() {
        return allowMerchantPayment;
    }

    public void setAllowMerchantPayment(Boolean allowMerchantPayment) {
        this.allowMerchantPayment = allowMerchantPayment;
    }

    /**
     * Vérifie quel mode est utilisé.
     *
     * @return true si une config existante est référencée, false sinon
     */
    public boolean isUsingExistingConfig() {
        return existingConfigId != null;
    }
}