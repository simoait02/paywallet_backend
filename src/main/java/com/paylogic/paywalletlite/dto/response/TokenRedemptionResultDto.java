package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO représentant le résultat du rachat d'un token individuel.
 */
public class TokenRedemptionResultDto {

    private UUID tokenId;
    private Boolean redeemed;
    private TransactionStatus finalStatus;
    private BigDecimal creditedAmount;
    private String failureReason;
    private String transactionHash;

    public TokenRedemptionResultDto() {}

    // Getters & Setters
    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public Boolean getRedeemed() { return redeemed; }
    public void setRedeemed(Boolean redeemed) { this.redeemed = redeemed; }

    public TransactionStatus getFinalStatus() { return finalStatus; }
    public void setFinalStatus(TransactionStatus finalStatus) { this.finalStatus = finalStatus; }

    public BigDecimal getCreditedAmount() { return creditedAmount; }
    public void setCreditedAmount(BigDecimal creditedAmount) { this.creditedAmount = creditedAmount; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getTransactionHash() { return transactionHash; }
    public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }
}