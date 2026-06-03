package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.transaction.enums.EntryType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO pour l'enregistrement d'une écriture comptable dans le grand livre.
 * Basé sur le principe de la comptabilité en partie double.
 */
public class LedgerEntryRequestDto {

    @NotNull(message = "L'identifiant du ledger est obligatoire")
    private UUID ledgerId;

    @NotNull(message = "L'identifiant de la transaction est obligatoire")
    private UUID transactionId;

    @NotNull(message = "L'identifiant du wallet est obligatoire")
    private UUID walletId;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;

    @NotNull(message = "Le type d'écriture est obligatoire")
    private EntryType entryType;

    /** Hash de l'entrée précédente pour chaînage immuable */
    private String previousEntryHash;

    public LedgerEntryRequestDto() {}

    // Getters & Setters
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

    public String getPreviousEntryHash() { return previousEntryHash; }
    public void setPreviousEntryHash(String previousEntryHash) { this.previousEntryHash = previousEntryHash; }
}