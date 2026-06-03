package com.paylogic.paywalletlite.domain.wallet;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.CurrencyCode;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;
import javax.persistence.*;
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

    // Relation JPA vers User (propriétaire du wallet)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Colonne brute pour usage offline (quand la relation n'est pas chargée)
    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    // Relation JPA vers WalletConfig
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_config_id", nullable = false)
    private WalletConfig walletConfig;

    // Colonne brute pour usage offline
    @Column(name = "wallet_config_id", insertable = false, updatable = false)
    private UUID walletConfigId;

    // Relation JPA vers TokenAllocationConfig
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_allocation_config_id", nullable = false)
    private TokenAllocationConfig tokenAllocationConfig;

    // Colonne brute pour usage offline
    @Column(name = "token_allocation_config_id", insertable = false, updatable = false)
    private UUID tokenAllocationConfigId;

    @Column(name = "online_balance", precision = 19, scale = 2)
    private BigDecimal onlineBalance;

    @Column(name = "offline_balance", precision = 19, scale = 2)
    private BigDecimal offlineBalance;

    @Column(name = "pending_balance", precision = 19, scale = 2)
    private BigDecimal pendingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private CurrencyCode currency;

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

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;



    public Wallet() {
        this.createdAt = LocalDateTime.now();
        this.status = WalletStatus.ACTIVE;
        this.onlineBalance = BigDecimal.ZERO;
        this.offlineBalance = BigDecimal.ZERO;
        this.pendingBalance = BigDecimal.ZERO;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public User getUser() { return user; }
    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getUserId();
        }
    }

    public UUID getUserId() {
        return userId != null ? userId :
                (user != null ? user.getUserId() : null);
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public WalletConfig getWalletConfig() { return walletConfig; }
    public void setWalletConfig(WalletConfig walletConfig) {
        this.walletConfig = walletConfig;
        if (walletConfig != null) {
            this.walletConfigId = walletConfig.getConfigId();
        }
    }

    public UUID getWalletConfigId() {
        return walletConfigId != null ? walletConfigId :
                (walletConfig != null ? walletConfig.getConfigId() : null);
    }

    public TokenAllocationConfig getTokenAllocationConfig() { return tokenAllocationConfig; }
    public void setTokenAllocationConfig(TokenAllocationConfig tokenAllocationConfig) {
        this.tokenAllocationConfig = tokenAllocationConfig;
        if (tokenAllocationConfig != null) {
            this.tokenAllocationConfigId = tokenAllocationConfig.getConfigId();
        }
    }

    public UUID getTokenAllocationConfigId() {
        return tokenAllocationConfigId != null ? tokenAllocationConfigId :
                (tokenAllocationConfig != null ? tokenAllocationConfig.getConfigId() : null);
    }

    public void setWalletConfigId(UUID walletConfigId) {
        this.walletConfigId = walletConfigId;
    }

    public void setTokenAllocationConfigId(UUID tokenAllocationConfigId) {
        this.tokenAllocationConfigId = tokenAllocationConfigId;
    }

    public BigDecimal getOnlineBalance() { return onlineBalance; }
    public void setOnlineBalance(BigDecimal onlineBalance) { this.onlineBalance = onlineBalance; }

    public BigDecimal getOfflineBalance() { return offlineBalance; }
    public void setOfflineBalance(BigDecimal offlineBalance) { this.offlineBalance = offlineBalance; }

    public BigDecimal getPendingBalance() { return pendingBalance; }
    public void setPendingBalance(BigDecimal pendingBalance) { this.pendingBalance = pendingBalance; }


    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }

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

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}