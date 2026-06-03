package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class TokenAllocationConfigRequestDto {

    @NotBlank(message = "Config name is required")
    private String configName;

    @NotNull(message = "Wallet type is required")
    private WalletType walletType;

    @NotNull(message = "Density threshold is required")
    @PositiveOrZero(message = "Density threshold must be positive or zero")
    private BigDecimal densityThreshold;

    @NotNull(message = "Sliding window size is required")
    @Positive(message = "Sliding window size must be positive")
    private Integer slidingWindowSize;

    @NotNull(message = "Max token count is required")
    @Positive(message = "Max token count must be positive")
    private Integer maxTokenCount;

    @NotNull(message = "Min single token value is required")
    @Positive(message = "Min single token value must be positive")
    private BigDecimal minSingleTokenValue;

    @NotNull(message = "Max single token value is required")
    @Positive(message = "Max single token value must be positive")
    private BigDecimal maxSingleTokenValue;

    @NotNull(message = "Max transfers per token is required")
    @PositiveOrZero(message = "Max transfers must be positive or zero")
    private Integer maxTransfersPerToken;

    @NotNull(message = "Token lifetime hours is required")
    @Positive(message = "Token lifetime hours must be positive")
    private Integer tokenLifetimeHours;

    private Boolean allowOverpayment;

    private BigDecimal maxOverpaymentThreshold;

    @NotNull(message = "Denomination IDs are required")
    private List<UUID> denominationIds;

    public TokenAllocationConfigRequestDto() {}

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

    public List<UUID> getDenominationIds() { return denominationIds; }
    public void setDenominationIds(List<UUID> denominationIds) { this.denominationIds = denominationIds; }
}