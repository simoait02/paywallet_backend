package com.paylogic.paywalletlite.domain.token;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token_transfer_nodes", schema = "pwl_app")
public class TokenTransferNode {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "transfer_node_id", updatable = false, nullable = false)
    private UUID transferNodeId;

    @Column(name = "token_id", nullable = false)
    private UUID tokenId;

    @Column(name = "payer_wallet_id", nullable = false)
    private UUID payerWalletId;

    @Column(name = "payee_wallet_id", nullable = false)
    private UUID payeeWalletId;

    @Column(name = "transferred_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal transferredAmount;

    @Column(name = "transfer_timestamp", nullable = false)
    private LocalDateTime transferTimestamp;

    @Column(name = "payer_signature", nullable = false, length = 4000)
    private String payerSignature;

    @Column(name = "payer_certificate", nullable = false, length = 4000)
    private String payerCertificate;

    @Column(name = "transfer_hash", nullable = false, length = 255)
    private String transferHash;

    public TokenTransferNode() {
        this.transferTimestamp = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getTransferNodeId() { return transferNodeId; }
    public void setTransferNodeId(UUID transferNodeId) { this.transferNodeId = transferNodeId; }

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public UUID getPayerWalletId() { return payerWalletId; }
    public void setPayerWalletId(UUID payerWalletId) { this.payerWalletId = payerWalletId; }

    public UUID getPayeeWalletId() { return payeeWalletId; }
    public void setPayeeWalletId(UUID payeeWalletId) { this.payeeWalletId = payeeWalletId; }

    public BigDecimal getTransferredAmount() { return transferredAmount; }
    public void setTransferredAmount(BigDecimal transferredAmount) { this.transferredAmount = transferredAmount; }

    public LocalDateTime getTransferTimestamp() { return transferTimestamp; }
    public void setTransferTimestamp(LocalDateTime transferTimestamp) { this.transferTimestamp = transferTimestamp; }

    public String getPayerSignature() { return payerSignature; }
    public void setPayerSignature(String payerSignature) { this.payerSignature = payerSignature; }

    public String getPayerCertificate() { return payerCertificate; }
    public void setPayerCertificate(String payerCertificate) { this.payerCertificate = payerCertificate; }

    public String getTransferHash() { return transferHash; }
    public void setTransferHash(String transferHash) { this.transferHash = transferHash; }
}