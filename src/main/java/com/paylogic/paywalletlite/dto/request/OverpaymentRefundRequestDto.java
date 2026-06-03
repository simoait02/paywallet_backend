package com.paylogic.paywalletlite.dto.request;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO pour la demande de remboursement d'un surpaiement.
 * Un surpaiement survient quand transferredAmount > requestedAmount.
 */
public class OverpaymentRefundRequestDto {

    @NotNull(message = "L'identifiant de la transaction originale est obligatoire")
    private UUID originalTransactionId;

    @NotNull(message = "L'identifiant du wallet destinataire du remboursement est obligatoire")
    private UUID walletId;

    @NotNull(message = "Le montant du remboursement est obligatoire")
    private BigDecimal refundAmount;

    /** Motif du remboursement */
    private String reason;

    /** Identifiant de l'administrateur traitant la demande */
    private String processedBy;

    public OverpaymentRefundRequestDto() {}

    // Getters & Setters
    public UUID getOriginalTransactionId() { return originalTransactionId; }
    public void setOriginalTransactionId(UUID originalTransactionId) { this.originalTransactionId = originalTransactionId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
}