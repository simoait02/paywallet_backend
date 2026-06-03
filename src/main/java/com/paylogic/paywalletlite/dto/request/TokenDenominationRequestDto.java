package com.paylogic.paywalletlite.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class TokenDenominationRequestDto {

    @NotNull(message = "Value is required")
    @Positive(message = "Value must be positive")
    private BigDecimal value;

    @NotBlank(message = "Currency code is required")
    private String currencyCode;

    @NotNull(message = "Priority order is required")
    @PositiveOrZero(message = "Priority order must be positive or zero")
    private Integer priorityOrder;

    private BigDecimal densityWeight;

    private BigDecimal minAllocationAmount;

    private BigDecimal maxAllocationAmount;

    public TokenDenominationRequestDto() {}

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Integer getPriorityOrder() { return priorityOrder; }
    public void setPriorityOrder(Integer priorityOrder) { this.priorityOrder = priorityOrder; }

    public BigDecimal getDensityWeight() { return densityWeight; }
    public void setDensityWeight(BigDecimal densityWeight) { this.densityWeight = densityWeight; }

    public BigDecimal getMinAllocationAmount() { return minAllocationAmount; }
    public void setMinAllocationAmount(BigDecimal minAllocationAmount) { this.minAllocationAmount = minAllocationAmount; }

    public BigDecimal getMaxAllocationAmount() { return maxAllocationAmount; }
    public void setMaxAllocationAmount(BigDecimal maxAllocationAmount) { this.maxAllocationAmount = maxAllocationAmount; }
}