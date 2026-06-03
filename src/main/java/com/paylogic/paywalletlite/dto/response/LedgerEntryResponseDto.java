package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.transaction.enums.EntryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une écriture comptable.
 */
public class LedgerEntryResponseDto {

    private UUID entryId;
    private UUID ledgerId;
    private UUID transactionId;
    private UUID walletId;
    private BigDecimal amount;
    private EntryType entryType;
    private LocalDateTime recordedAt;
    private String entryHash;
    private Integer sequenceNumber;
    private String previousEntryHash;

    public LedgerEntryResponseDto() {}

    // Getters & Setters
    public UUID getEntryId() { return entryId; }
    public void setEntryId(UUID entryId) { this.entryId = entryId; }

    public UUID getLedgerId() { return ledgerId; }
    public void setLedgerId(UUID ledgerId) { this.ledgerId = ledgerId; }

    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public EntryType getEntryType() { return entryType; }
    public void setEntryType(EntryType entryType) { this.entryType = entryType; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public String getEntryHash() { return entryHash; }
    public void setEntryHash(String entryHash) { this.entryHash = entryHash; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public String getPreviousEntryHash() { return previousEntryHash; }
    public void setPreviousEntryHash(String previousEntryHash) { this.previousEntryHash = previousEntryHash; }
}