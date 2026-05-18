package com.paylogic.paywalletlite.domain.transaction;

import com.paylogic.paywalletlite.domain.transaction.enums.OverpaymentStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionType;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions", schema = "pwl_app")
public class Transaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "sender_wallet_id", nullable = false)
    private UUID senderWalletId;

    @Column(name = "receiver_wallet_id", nullable = false)
    private UUID receiverWalletId;

    @Column(name = "requested_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal requestedAmount;

    @Column(name = "transferred_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal transferredAmount;

    @Column(name = "overpayment_amount", precision = 19, scale = 2)
    private BigDecimal overpaymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "overpayment_status", length = 20)
    private OverpaymentStatus overpaymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_offline", nullable = false)
    private Boolean isOffline;

    @Column(name = "offline_signature", length = 4000)
    private String offlineSignature;

    @Column(name = "sync_batch_id")
    private UUID syncBatchId;

    @Column(name = "transaction_hash", length = 255)
    private String transactionHash;

    public Transaction() {
        this.initiatedAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.isOffline = false;
        this.overpaymentStatus = OverpaymentStatus.PENDING_REFUND;
    }

    // Getters et Setters
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

    public String getOfflineSignature() { return offlineSignature; }
    public void setOfflineSignature(String offlineSignature) { this.offlineSignature = offlineSignature; }

    public UUID getSyncBatchId() { return syncBatchId; }
    public void setSyncBatchId(UUID syncBatchId) { this.syncBatchId = syncBatchId; }

    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
}