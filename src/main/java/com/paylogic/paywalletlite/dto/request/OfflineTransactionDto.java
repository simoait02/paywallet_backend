package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.transaction.enums.TransactionType;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO représentant une transaction offline individuelle dans un SyncBatch.
 *
 * Contient les tokens avec leur chaîne de transfert complète
 * pour permettre la vérification côté serveur.
 */
public class OfflineTransactionDto {

    /** ID local de la transaction (généré par le device) */
    @NotNull(message = "Local transaction ID is required")
    private UUID localTransactionId;

    /** Wallet émetteur (premier propriétaire du token) */
    @NotNull(message = "Sender wallet ID is required")
    private UUID senderWalletId;

    /** Wallet destinataire (celui qui synchronise) */
    @NotNull(message = "Receiver wallet ID is required")
    private UUID receiverWalletId;

    /** Montant demandé par le payeur */
    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    private BigDecimal requestedAmount;

    /** Type de transaction */
    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    /** Signature ECDSA du payeur sur le payload de transfert */
    @NotNull(message = "Payer signature is required")
    private String payerSignature;

    /** Timestamp du transfert (heure device) */
    @NotNull(message = "Transfer timestamp is required")
    private String transferTimestamp;

    /**
     * Tokens transférés avec leur chaîne de transfert complète.
     * Chaque token contient tous les TokenTransferNode accumulés.
     */
    @NotEmpty(message = "At least one token is required")
    @Valid
    private List<OfflineTokenDto> tokens;

    public OfflineTransactionDto() {}

    // Getters et Setters
    public UUID getLocalTransactionId() { return localTransactionId; }
    public void setLocalTransactionId(UUID localTransactionId) { this.localTransactionId = localTransactionId; }

    public UUID getSenderWalletId() { return senderWalletId; }
    public void setSenderWalletId(UUID senderWalletId) { this.senderWalletId = senderWalletId; }

    public UUID getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(UUID receiverWalletId) { this.receiverWalletId = receiverWalletId; }

    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public String getPayerSignature() { return payerSignature; }
    public void setPayerSignature(String payerSignature) { this.payerSignature = payerSignature; }

    public String getTransferTimestamp() { return transferTimestamp; }
    public void setTransferTimestamp(String transferTimestamp) { this.transferTimestamp = transferTimestamp; }

    public List<OfflineTokenDto> getTokens() { return tokens; }
    public void setTokens(List<OfflineTokenDto> tokens) { this.tokens = tokens; }
}