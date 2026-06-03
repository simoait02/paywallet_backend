package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.wallet.enums.CurrencyCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO pour la requête de financement d'un wallet.
 */
public class FundWalletRequestDto {

    /**
     * ID du wallet à financer.
     * Obligatoire si l'appelant est un administrateur.
     * Null si l'utilisateur finance son propre wallet.
     */
    private UUID walletId;

    /**
     * Montant à créditer sur le wallet.
     * Doit être strictement positif.
     */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private CurrencyCode currency;

    /**
     * Source du financement.
     * Exemples : AGENT_CASH_IN, BANK_TRANSFER, MOBILE_MONEY, ADMIN_CREDIT, REFUND
     */
    @NotNull(message = "Funding source is required")
    private String fundingSource;

    /**
     * Référence externe (optionnelle).
     * Exemple : numéro de transaction bancaire, ID de transaction agent.
     */
    private String externalReference;

    /**
     * Notes ou commentaires sur le financement.
     */
    private String notes;

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(String fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }
}