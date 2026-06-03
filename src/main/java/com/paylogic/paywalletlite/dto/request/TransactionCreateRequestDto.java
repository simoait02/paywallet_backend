package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.transaction.enums.TransactionType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO pour la création d'une transaction (online ou offline).
 * Aligné avec le workflow 3 phases du rapport PFE.
 */
public class TransactionCreateRequestDto {

    @NotNull(message = "L'identifiant du wallet expéditeur est obligatoire")
    private UUID senderWalletId;

    @NotNull(message = "L'identifiant du wallet destinataire est obligatoire")
    private UUID receiverWalletId;

    @NotNull(message = "Le montant demandé est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal requestedAmount;

    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal transferredAmount;

    @NotNull(message = "Le type de transaction est obligatoire")
    private TransactionType type;

    private Boolean isOffline = false;

    /** Signature ECDSA P-256 du payload offline (Phase 2) */
    private String offlineSignature;

    /** Payload token crypté pour transactions offline */
    private String tokenPayload;

    /** Identifiants des tokens transférés (pour traçabilité) */
    private String[] tokenIds;

    public TransactionCreateRequestDto() {}

    // Getters & Setters
    public UUID getSenderWalletId() { return senderWalletId; }
    public void setSenderWalletId(UUID senderWalletId) { this.senderWalletId = senderWalletId; }

    public UUID getReceiverWalletId() { return receiverWalletId; }
    public void setReceiverWalletId(UUID receiverWalletId) { this.receiverWalletId = receiverWalletId; }

    public BigDecimal getRequestedAmount() { return requestedAmount; }
    public void setRequestedAmount(BigDecimal requestedAmount) { this.requestedAmount = requestedAmount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Boolean getIsOffline() { return isOffline; }
    public void setIsOffline(Boolean isOffline) { this.isOffline = isOffline; }

    public String getOfflineSignature() { return offlineSignature; }
    public void setOfflineSignature(String offlineSignature) { this.offlineSignature = offlineSignature; }

    public String getTokenPayload() { return tokenPayload; }
    public void setTokenPayload(String tokenPayload) { this.tokenPayload = tokenPayload; }

    public String[] getTokenIds() { return tokenIds; }
    public void setTokenIds(String[] tokenIds) { this.tokenIds = tokenIds; }

    public boolean getTransferredAmount() {
        return false;
    }
    public void setTransferredAmount(BigDecimal transferredAmount) {
        this.transferredAmount = transferredAmount;
    }

    public Boolean getOffline() {
        return isOffline;
    }

    public void setOffline(Boolean offline) {
        isOffline = offline;
    }
}