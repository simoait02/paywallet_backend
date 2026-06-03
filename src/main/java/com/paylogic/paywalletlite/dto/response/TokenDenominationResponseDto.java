package com.paylogic.paywalletlite.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TokenDenominationResponseDto {

    private UUID denominationId;
    private BigDecimal value;
    private String currencyCode;
    private Boolean isActive;
    private Integer priorityOrder;
    private BigDecimal densityWeight;
    private BigDecimal minAllocationAmount;
    private BigDecimal maxAllocationAmount;
    private LocalDateTime createdAt;

    public TokenDenominationResponseDto() {}

    public UUID getDenominationId() { return denominationId; }
    public void setDenominationId(UUID denominationId) { this.denominationId = denominationId; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getPriorityOrder() { return priorityOrder; }
    public void setPriorityOrder(Integer priorityOrder) { this.priorityOrder = priorityOrder; }

    public BigDecimal getDensityWeight() { return densityWeight; }
    public void setDensityWeight(BigDecimal densityWeight) { this.densityWeight = densityWeight; }

    public BigDecimal getMinAllocationAmount() { return minAllocationAmount; }
    public void setMinAllocationAmount(BigDecimal minAllocationAmount) { this.minAllocationAmount = minAllocationAmount; }

    public BigDecimal getMaxAllocationAmount() { return maxAllocationAmount; }
    public void setMaxAllocationAmount(BigDecimal maxAllocationAmount) { this.maxAllocationAmount = maxAllocationAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}