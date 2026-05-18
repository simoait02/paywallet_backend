package com.paylogic.paywalletlite.domain.token;

import com.paylogic.paywalletlite.domain.token.enums.TokenAllocationConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token_allocation_configs", schema = "pwl_app")
public class TokenAllocationConfig {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "config_id", updatable = false, nullable = false)
    private UUID configId;

    @Column(name = "config_name", nullable = false, length = 100)
    private String configName;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 20)
    private WalletType walletType;

    @Column(name = "denominations")
    private String denominations;  // Stocké comme JSON ou CSV

    @Column(name = "density_threshold", precision = 3, scale = 2)
    private BigDecimal densityThreshold;

    @Column(name = "sliding_window_size")
    private Integer slidingWindowSize;

    @Column(name = "max_token_count")
    private Integer maxTokenCount;

    @Column(name = "min_single_token_value", precision = 19, scale = 2)
    private BigDecimal minSingleTokenValue;

    @Column(name = "max_single_token_value", precision = 19, scale = 2)
    private BigDecimal maxSingleTokenValue;

    @Column(name = "max_transfers_per_token")
    private Integer maxTransfersPerToken;

    @Column(name = "token_lifetime_hours")
    private Integer tokenLifetimeHours;

    @Column(name = "allow_overpayment")
    private Boolean allowOverpayment;

    @Column(name = "max_overpayment_threshold", precision = 19, scale = 2)
    private BigDecimal maxOverpaymentThreshold;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TokenAllocationConfigStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public TokenAllocationConfig() {
        this.createdAt = LocalDateTime.now();
        this.status = TokenAllocationConfigStatus.ACTIVE;
    }

    // Getters et Setters
    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }

    public WalletType getWalletType() { return walletType; }
    public void setWalletType(WalletType walletType) { this.walletType = walletType; }

    public String getDenominations() { return denominations; }
    public void setDenominations(String denominations) { this.denominations = denominations; }

    public BigDecimal getDensityThreshold() { return densityThreshold; }
    public void setDensityThreshold(BigDecimal densityThreshold) { this.densityThreshold = densityThreshold; }

    public Integer getSlidingWindowSize() { return slidingWindowSize; }
    public void setSlidingWindowSize(Integer slidingWindowSize) { this.slidingWindowSize = slidingWindowSize; }

    public Integer getMaxTokenCount() { return maxTokenCount; }
    public void setMaxTokenCount(Integer maxTokenCount) { this.maxTokenCount = maxTokenCount; }

    public BigDecimal getMinSingleTokenValue() { return minSingleTokenValue; }
    public void setMinSingleTokenValue(BigDecimal minSingleTokenValue) { this.minSingleTokenValue = minSingleTokenValue; }

    public BigDecimal getMaxSingleTokenValue() { return maxSingleTokenValue; }
    public void setMaxSingleTokenValue(BigDecimal maxSingleTokenValue) { this.maxSingleTokenValue = maxSingleTokenValue; }

    public Integer getMaxTransfersPerToken() { return maxTransfersPerToken; }
    public void setMaxTransfersPerToken(Integer maxTransfersPerToken) { this.maxTransfersPerToken = maxTransfersPerToken; }

    public Integer getTokenLifetimeHours() { return tokenLifetimeHours; }
    public void setTokenLifetimeHours(Integer tokenLifetimeHours) { this.tokenLifetimeHours = tokenLifetimeHours; }

    public Boolean getAllowOverpayment() { return allowOverpayment; }
    public void setAllowOverpayment(Boolean allowOverpayment) { this.allowOverpayment = allowOverpayment; }

    public BigDecimal getMaxOverpaymentThreshold() { return maxOverpaymentThreshold; }
    public void setMaxOverpaymentThreshold(BigDecimal maxOverpaymentThreshold) { this.maxOverpaymentThreshold = maxOverpaymentThreshold; }

    public TokenAllocationConfigStatus getStatus() { return status; }
    public void setStatus(TokenAllocationConfigStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}