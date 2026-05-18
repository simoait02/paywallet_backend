package com.paylogic.paywalletlite.domain.credit;

import com.paylogic.paywalletlite.domain.credit.enums.RepaymentStatus;
import com.paylogic.paywalletlite.domain.credit.enums.RepaymentType;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_repayments", schema = "pwl_app")
public class CreditRepayment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "repayment_id", updatable = false, nullable = false)
    private UUID repaymentId;

    @Column(name = "credit_id", nullable = false)
    private UUID creditId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private RepaymentType type;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "remaining_balance", precision = 19, scale = 2)
    private BigDecimal remainingBalance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RepaymentStatus status;

    public CreditRepayment() {
        this.status = RepaymentStatus.PENDING;
    }

    // Getters et Setters
    public UUID getRepaymentId() { return repaymentId; }
    public void setRepaymentId(UUID repaymentId) { this.repaymentId = repaymentId; }

    public UUID getCreditId() { return creditId; }
    public void setCreditId(UUID creditId) { this.creditId = creditId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public RepaymentType getType() { return type; }
    public void setType(RepaymentType type) { this.type = type; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }

    public RepaymentStatus getStatus() { return status; }
    public void setStatus(RepaymentStatus status) { this.status = status; }
}