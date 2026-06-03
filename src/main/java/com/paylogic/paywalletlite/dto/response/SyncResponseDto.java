package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.transaction.enums.SyncBatchStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de réponse pour une synchronisation (Phase 3: Reconciliation).
 */
public class SyncResponseDto {

    private UUID batchId;
    private UUID walletId;
    private SyncBatchStatus status;
    private Integer transactionCount;
    private Integer successCount;
    private Integer failureCount;
    private BigDecimal totalAmountSynced;
    private BigDecimal totalAmountFailed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<TokenRedemptionResultDto> redemptionResults;
    private String serverSignature;
    private String discrepancyReport;

    public SyncResponseDto() {}

    // Getters & Setters
    public UUID getBatchId() { return batchId; }
    public void setBatchId(UUID batchId) { this.batchId = batchId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public SyncBatchStatus getStatus() { return status; }
    public void setStatus(SyncBatchStatus status) { this.status = status; }

    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }

    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

    public Integer getFailureCount() { return failureCount; }
    public void setFailureCount(Integer failureCount) { this.failureCount = failureCount; }

    public BigDecimal getTotalAmountSynced() { return totalAmountSynced; }
    public void setTotalAmountSynced(BigDecimal totalAmountSynced) { this.totalAmountSynced = totalAmountSynced; }

    public BigDecimal getTotalAmountFailed() { return totalAmountFailed; }
    public void setTotalAmountFailed(BigDecimal totalAmountFailed) { this.totalAmountFailed = totalAmountFailed; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<TokenRedemptionResultDto> getRedemptionResults() { return redemptionResults; }
    public void setRedemptionResults(List<TokenRedemptionResultDto> redemptionResults) { this.redemptionResults = redemptionResults; }

    public String getServerSignature() { return serverSignature; }
    public void setServerSignature(String serverSignature) { this.serverSignature = serverSignature; }

    public String getDiscrepancyReport() { return discrepancyReport; }
    public void setDiscrepancyReport(String discrepancyReport) { this.discrepancyReport = discrepancyReport; }
}