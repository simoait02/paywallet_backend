package com.paylogic.paywalletlite.dto.request;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO représentant un nœud de transfert dans la chaîne de propriété d'un token.
 *
 * Chaque nœud est signé par le payeur, ce qui garantit la non-répudiation.
 * La séquence (sequenceNumber) permet de reconstituer l'ordre des transferts.
 */
public class TransferNodeDto {

    /** Wallet payeur (celui qui transfère le token) */
    @NotNull(message = "Payer wallet ID is required")
    private UUID payerWalletId;

    /** Wallet bénéficiaire (celui qui reçoit le token) */
    @NotNull(message = "Payee wallet ID is required")
    private UUID payeeWalletId;

    /** Montant transféré (en cas de split partiel) */
    @NotNull(message = "Transferred amount is required")
    private BigDecimal transferredAmount;

    /** Timestamp du transfert (heure device) */
    @NotNull(message = "Transfer timestamp is required")
    private String transferTimestamp;

    /** Signature ECDSA du payeur sur le payload de transfert */
    @NotNull(message = "Payer signature is required")
    private String payerSignature;

    /** Certificat du payeur (pour vérification de signature) */
    @NotNull(message = "Payer certificate is required")
    private String payerCertificate;

    /** Hash du transfert pour vérification d'intégrité */
    @NotNull(message = "Transfer hash is required")
    private String transferHash;

    /** Numéro de séquence dans la chaîne (0 = émission initiale) */
    @NotNull(message = "Sequence number is required")
    private Integer sequenceNumber;

    public TransferNodeDto() {}

    // Getters et Setters
    public UUID getPayerWalletId() { return payerWalletId; }
    public void setPayerWalletId(UUID payerWalletId) { this.payerWalletId = payerWalletId; }

    public UUID getPayeeWalletId() { return payeeWalletId; }
    public void setPayeeWalletId(UUID payeeWalletId) { this.payeeWalletId = payeeWalletId; }

    public BigDecimal getTransferredAmount() { return transferredAmount; }
    public void setTransferredAmount(BigDecimal transferredAmount) { this.transferredAmount = transferredAmount; }

    public String getTransferTimestamp() { return transferTimestamp; }
    public void setTransferTimestamp(String transferTimestamp) { this.transferTimestamp = transferTimestamp; }

    public String getPayerSignature() { return payerSignature; }
    public void setPayerSignature(String payerSignature) { this.payerSignature = payerSignature; }

    public String getPayerCertificate() { return payerCertificate; }
    public void setPayerCertificate(String payerCertificate) { this.payerCertificate = payerCertificate; }

    public String getTransferHash() { return transferHash; }
    public void setTransferHash(String transferHash) { this.transferHash = transferHash; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }
}