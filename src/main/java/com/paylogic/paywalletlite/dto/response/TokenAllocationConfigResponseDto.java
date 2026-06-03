package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.token.enums.TokenAllocationConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenAllocationConfigResponseDto {

    private UUID configId;
    private String configName;
    private WalletType walletType;
    private BigDecimal densityThreshold;
    private Integer slidingWindowSize;
    private Integer maxTokenCount;
    private BigDecimal minSingleTokenValue;
    private BigDecimal maxSingleTokenValue;
    private Integer maxTransfersPerToken;
    private Integer tokenLifetimeHours;
    private Boolean allowOverpayment;
    private BigDecimal maxOverpaymentThreshold;
    private TokenAllocationConfigStatus status;
    private LocalDateTime createdAt;
    private String createdBy;
    private List<TokenDenominationResponseDto> denominations = new ArrayList<TokenDenominationResponseDto>();

    public TokenAllocationConfigResponseDto() {}

    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }

    public WalletType getWalletType() { return walletType; }
    public void setWalletType(WalletType walletType) { this.walletType = walletType; }

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

    public List<TokenDenominationResponseDto> getDenominations() { return denominations; }
    public void setDenominations(List<TokenDenominationResponseDto> denominations) { this.denominations = denominations; }
}