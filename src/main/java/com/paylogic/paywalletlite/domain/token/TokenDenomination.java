package com.paylogic.paywalletlite.domain.token;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token_denominations", schema = "pwl_app")
public class TokenDenomination {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "denomination_id", updatable = false, nullable = false)
    private UUID denominationId;

    @Column(name = "value", nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder;

    @Column(name = "min_allocation_amount", precision = 19, scale = 2)
    private BigDecimal minAllocationAmount;

    @Column(name = "max_allocation_amount", precision = 19, scale = 2)
    private BigDecimal maxAllocationAmount;

    @Column(name = "density_weight", precision = 5, scale = 2)
    private BigDecimal densityWeight;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.denominationId == null) {
            this.denominationId = UUID.randomUUID();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters & Setters
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

    public BigDecimal getMinAllocationAmount() { return minAllocationAmount; }
    public void setMinAllocationAmount(BigDecimal minAllocationAmount) { this.minAllocationAmount = minAllocationAmount; }

    public BigDecimal getMaxAllocationAmount() { return maxAllocationAmount; }
    public void setMaxAllocationAmount(BigDecimal maxAllocationAmount) { this.maxAllocationAmount = maxAllocationAmount; }

    public BigDecimal getDensityWeight() { return densityWeight; }
    public void setDensityWeight(BigDecimal densityWeight) { this.densityWeight = densityWeight; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}