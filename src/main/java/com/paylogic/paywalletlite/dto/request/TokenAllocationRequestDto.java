package com.paylogic.paywalletlite.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class TokenAllocationRequestDto {

    private BigDecimal amount;
    private List<BigDecimal> preferredDenominations;
    private Integer lifetimeHours;
    private Integer maxTransfers;

    public TokenAllocationRequestDto() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public List<BigDecimal> getPreferredDenominations() { return preferredDenominations; }
    public void setPreferredDenominations(List<BigDecimal> preferredDenominations) { this.preferredDenominations = preferredDenominations; }

    public Integer getLifetimeHours() { return lifetimeHours; }
    public void setLifetimeHours(Integer lifetimeHours) { this.lifetimeHours = lifetimeHours; }

    public Integer getMaxTransfers() { return maxTransfers; }
    public void setMaxTransfers(Integer maxTransfers) { this.maxTransfers = maxTransfers; }
}