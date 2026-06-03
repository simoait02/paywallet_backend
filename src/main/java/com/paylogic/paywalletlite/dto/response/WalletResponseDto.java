package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.wallet.enums.CurrencyCode;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WalletResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID walletId;
    private UUID userId;
    private WalletType walletType;
    private WalletStatus status;
    private BigDecimal onlineBalance;
    private BigDecimal offlineBalance;
    private BigDecimal pendingBalance;
    private CurrencyCode currency;
    private LocalDateTime createdAt;
    private String publicKey;
    private String rejectionReason;

    // Config imbriquée pour afficher les limites directement
    private WalletConfigResponseDto config;

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public WalletStatus getStatus() {
        return status;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public BigDecimal getOnlineBalance() {
        return onlineBalance;
    }

    public void setOnlineBalance(BigDecimal onlineBalance) {
        this.onlineBalance = onlineBalance;
    }

    public BigDecimal getOfflineBalance() {
        return offlineBalance;
    }

    public void setOfflineBalance(BigDecimal offlineBalance) {
        this.offlineBalance = offlineBalance;
    }

    public BigDecimal getPendingBalance() {
        return pendingBalance;
    }

    public void setPendingBalance(BigDecimal pendingBalance) {
        this.pendingBalance = pendingBalance;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    // Nouveau getter/setter
    public WalletConfigResponseDto getConfig() { return config; }
    public void setConfig(WalletConfigResponseDto config) { this.config = config; }
}