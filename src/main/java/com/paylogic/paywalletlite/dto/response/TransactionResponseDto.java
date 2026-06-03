package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.transaction.enums.OverpaymentStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une transaction.
 */
public class TransactionResponseDto {

    private UUID transactionId;
    private UUID senderWalletId;
    private UUID receiverWalletId;
    private BigDecimal requestedAmount;
    private BigDecimal transferredAmount;
    private BigDecimal overpaymentAmount;
    private OverpaymentStatus overpaymentStatus;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime initiatedAt;
    private LocalDateTime completedAt;
    private Boolean isOffline;
    private UUID syncBatchId;
    private String transactionHash;

    public TransactionResponseDto() {}

    // Getters & Setters
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public UUID getSenderWalletId() { return senderWalletId; }
    public void setSenderWalletId(UUID senderWalletId) { this.senderWalletId = senderWalletId; }

    public UUID getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(UUID receiverWalletId) { this.receiverWalletId = receiverWalletId; }

    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }

    public BigDecimal getTransferredAmount() { return transferredAmount; }
    public void setTransferredAmount(BigDecimal transferredAmount) { this.transferredAmount = transferredAmount; }

    public BigDecimal getOverpaymentAmount() { return overpaymentAmount; }
    public void setOverpaymentAmount(BigDecimal overpaymentAmount) { this.overpaymentAmount = overpaymentAmount; }

    public OverpaymentStatus getOverpaymentStatus() { return overpaymentStatus; }
    public void setOverpaymentStatus(OverpaymentStatus overpaymentStatus) { this.overpaymentStatus = overpaymentStatus; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public Boolean getIsOffline() { return isOffline; }
    public void setIsOffline(Boolean isOffline) { this.isOffline = isOffline; }

    public UUID getSyncBatchId() { return syncBatchId; }
    public void setSyncBatchId(UUID syncBatchId) { this.syncBatchId = syncBatchId; }

    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
}