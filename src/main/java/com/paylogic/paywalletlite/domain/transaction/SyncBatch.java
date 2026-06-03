package com.paylogic.paywalletlite.domain.transaction;

import com.paylogic.paywalletlite.domain.transaction.enums.SyncBatchStatus;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sync_batches", schema = "pwl_app")
public class SyncBatch {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "batch_id", updatable = false, nullable = false)
    private UUID batchId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SyncBatchStatus status;

    @Column(name = "server_signature", length = 4000)
    private String serverSignature;

    @Column(name = "expected_balance_before", precision = 19, scale = 2)
    private BigDecimal expectedBalanceBefore;

    @Column(name = "expected_balance_after", precision = 19, scale = 2)
    private BigDecimal expectedBalanceAfter;

    @Column(name = "actual_balance_after", precision = 19, scale = 2)
    private BigDecimal actualBalanceAfter;

    @Column(name = "discrepancy", precision = 19, scale = 2)
    private BigDecimal discrepancy;

    public SyncBatch() {
        this.startedAt = LocalDateTime.now();
        this.status = SyncBatchStatus.INITIATED;
    }

    // Getters et Setters
    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public SyncBatchStatus getStatus() { return status; }
    public void setStatus(SyncBatchStatus status) { this.status = status; }

    public String getServerSignature() { return serverSignature; }
    public void setServerSignature(String serverSignature) { this.serverSignature = serverSignature; }

    public BigDecimal getExpectedBalanceBefore() { return expectedBalanceBefore; }
    public void setExpectedBalanceBefore(BigDecimal expectedBalanceBefore) { this.expectedBalanceBefore = expectedBalanceBefore; }

    public BigDecimal getExpectedBalanceAfter() { return expectedBalanceAfter; }
    public void setExpectedBalanceAfter(BigDecimal expectedBalanceAfter) { this.expectedBalanceAfter = expectedBalanceAfter; }

    public BigDecimal getActualBalanceAfter() { return actualBalanceAfter; }
    public void setActualBalanceAfter(BigDecimal actualBalanceAfter) { this.actualBalanceAfter = actualBalanceAfter; }

    public BigDecimal getDiscrepancy() { return discrepancy; }
    public void setDiscrepancy(BigDecimal discrepancy) { this.discrepancy = discrepancy; }
}