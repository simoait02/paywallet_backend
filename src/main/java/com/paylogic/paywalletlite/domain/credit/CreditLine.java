package com.paylogic.paywalletlite.domain.credit;

import com.paylogic.paywalletlite.domain.credit.enums.CreditStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_lines", schema = "pwl_app")
public class CreditLine {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "credit_id", updatable = false, nullable = false)
    private UUID creditId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "config_id", nullable = false)
    private UUID configId;

    @Column(name = "approved_limit", precision = 19, scale = 2, nullable = false)
    private BigDecimal approvedLimit;

    @Column(name = "used_amount", precision = 19, scale = 2)
    private BigDecimal usedAmount;

    @Column(name = "available_amount", precision = 19, scale = 2)
    private BigDecimal availableAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CreditStatus status;

    @Column(name = "approved_at", nullable = false)
    private LocalDateTime approvedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "approved_by", length = 100)
    private String approvedBy;

    @Column(name = "applied_interest_rate", precision = 5, scale = 2)
    private BigDecimal appliedInterestRate;

    @Column(name = "repayment_period_days")
    private Integer repaymentPeriodDays;

    @Column(name = "last_repayment_at")
    private LocalDateTime lastRepaymentAt;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    public CreditLine() {
        this.approvedAt = LocalDateTime.now();
        this.status = CreditStatus.UNUSED;
        this.usedAmount = BigDecimal.ZERO;
        this.availableAmount = BigDecimal.ZERO;
    }

    // Getters et Setters
    public UUID getCreditId() { return creditId; }
    public void setCreditId(UUID creditId) { this.creditId = creditId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public BigDecimal getApprovedLimit() { return approvedLimit; }
    public void setApprovedLimit(BigDecimal approvedLimit) { this.approvedLimit = approvedLimit; }

    public BigDecimal getUsedAmount() { return usedAmount; }
    public void setUsedAmount(BigDecimal usedAmount) { this.usedAmount = usedAmount; }

    public BigDecimal getAvailableAmount() { return availableAmount; }
    public void setAvailableAmount(BigDecimal availableAmount) { this.availableAmount = availableAmount; }

    public CreditStatus getStatus() { return status; }
    public void setStatus(CreditStatus status) { this.status = status; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public BigDecimal getAppliedInterestRate() { return appliedInterestRate; }
    public void setAppliedInterestRate(BigDecimal appliedInterestRate) { this.appliedInterestRate = appliedInterestRate; }

    public Integer getRepaymentPeriodDays() { return repaymentPeriodDays; }
    public void setRepaymentPeriodDays(Integer repaymentPeriodDays) { this.repaymentPeriodDays = repaymentPeriodDays; }

    public LocalDateTime getLastRepaymentAt() { return lastRepaymentAt; }
    public void setLastRepaymentAt(LocalDateTime lastRepaymentAt) { this.lastRepaymentAt = lastRepaymentAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}