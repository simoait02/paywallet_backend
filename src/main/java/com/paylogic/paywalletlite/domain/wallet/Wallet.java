package com.paylogic.paywalletlite.domain.wallet;

import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallets", schema = "pwl_app")
public class Wallet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "wallet_id", updatable = false, nullable = false)
    private UUID walletId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "wallet_config_id", nullable = false)
    private UUID walletConfigId;

    @Column(name = "token_allocation_config_id", nullable = false)
    private UUID tokenAllocationConfigId;

    @Column(name = "online_balance", precision = 19, scale = 2)
    private BigDecimal onlineBalance;

    @Column(name = "offline_balance", precision = 19, scale = 2)
    private BigDecimal offlineBalance;

    @Column(name = "pending_balance", precision = 19, scale = 2)
    private BigDecimal pendingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WalletStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_sync_timestamp")
    private LocalDateTime lastSyncTimestamp;

    @Column(name = "public_key", length = 4000)
    private String publicKey;

    @Column(name = "certificate_id", length = 255)
    private String certificateId;

    @Column(name = "current_keypair_id")
    private UUID currentKeypairId;

    public Wallet() {
        this.createdAt = LocalDateTime.now();
        this.status = WalletStatus.ACTIVE;
        this.onlineBalance = BigDecimal.ZERO;
        this.offlineBalance = BigDecimal.ZERO;
        this.pendingBalance = BigDecimal.ZERO;
    }

    // Getters et Setters
    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getWalletConfigId() { return walletConfigId; }
    public void setWalletConfigId(UUID walletConfigId) { this.walletConfigId = walletConfigId; }

    public UUID getTokenAllocationConfigId() { return tokenAllocationConfigId; }
    public void setTokenAllocationConfigId(UUID tokenAllocationConfigId) { this.tokenAllocationConfigId = tokenAllocationConfigId; }

    public BigDecimal getOnlineBalance() { return onlineBalance; }
    public void setOnlineBalance(BigDecimal onlineBalance) { this.onlineBalance = onlineBalance; }

    public BigDecimal getOfflineBalance() { return offlineBalance; }
    public void setOfflineBalance(BigDecimal offlineBalance) { this.offlineBalance = offlineBalance; }

    public BigDecimal getPendingBalance() { return pendingBalance; }
    public void setPendingBalance(BigDecimal pendingBalance) { this.pendingBalance = pendingBalance; }

    public WalletStatus getStatus() { return status; }
    public void setStatus(WalletStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastSyncTimestamp() { return lastSyncTimestamp; }
    public void setLastSyncTimestamp(LocalDateTime lastSyncTimestamp) { this.lastSyncTimestamp = lastSyncTimestamp; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public UUID getCurrentKeypairId() { return currentKeypairId; }
    public void setCurrentKeypairId(UUID currentKeypairId) { this.currentKeypairId = currentKeypairId; }
}