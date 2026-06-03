package com.paylogic.paywalletlite.domain.token;

import com.paylogic.paywalletlite.domain.token.enums.TokenAllocationConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity
@Table(name = "TOKEN_ALLOCATION_CONFIGS")
public class TokenAllocationConfig {

    @Id
    @Column(name = "config_id")
    private UUID configId;

    @Column(name = "config_name", nullable = false, unique = true, length = 100)
    private String configName;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type", nullable = false, length = 20)
    private WalletType walletType;

    // 🔥 REMPLACÉ : ancien `Decimal[] denominations` → relation Many-to-Many vers TOKEN_DENOMINATIONS
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "CONFIG_DENOMINATIONS",
            joinColumns = @JoinColumn(name = "config_id"),
            inverseJoinColumns = @JoinColumn(name = "denomination_id")
    )
    @OrderBy("priorityOrder DESC")
    private Set<TokenDenomination> denominations = new HashSet<>();

    @Column(name = "density_threshold", nullable = false, precision = 5, scale = 2)
    private BigDecimal densityThreshold;

    @Column(name = "sliding_window_size", nullable = false)
    private Integer slidingWindowSize;

    @Column(name = "max_token_count", nullable = false)
    private Integer maxTokenCount;

    @Column(name = "min_single_token_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal minSingleTokenValue;

    @Column(name = "max_single_token_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal maxSingleTokenValue;

    @Column(name = "max_transfers_per_token", nullable = false)
    private Integer maxTransfersPerToken;

    @Column(name = "token_lifetime_hours", nullable = false)
    private Integer tokenLifetimeHours;

    @Column(name = "allow_overpayment", nullable = false)
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

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.configId == null) {
            this.configId = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 🔥 MÉTHODE UTILITAIRE : récupérer les valeurs triées par priorité décroissante
    @Transient
    public List<BigDecimal> getSortedDenominationValues() {
        return denominations.stream()
                .filter(new Predicate<TokenDenomination>() {
                    @Override
                    public boolean test(TokenDenomination d) {
                        return d.getIsActive() != null && d.getIsActive();
                    }
                })
                .sorted(new Comparator<TokenDenomination>() {
                    @Override
                    public int compare(TokenDenomination d1, TokenDenomination d2) {
                        return Integer.compare(d2.getPriorityOrder(), d1.getPriorityOrder());
                    }
                })
                .map(new Function<TokenDenomination, BigDecimal>() {
                    @Override
                    public BigDecimal apply(TokenDenomination d) {
                        return d.getValue();
                    }
                })
                .collect(Collectors.toList());
    }

    // Getters & Setters
    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }

    public WalletType getWalletType() { return walletType; }
    public void setWalletType(WalletType walletType) { this.walletType = walletType; }

    public Set<TokenDenomination> getDenominations() { return denominations; }
    public void setDenominations(Set<TokenDenomination> denominations) { this.denominations = denominations; }

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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}