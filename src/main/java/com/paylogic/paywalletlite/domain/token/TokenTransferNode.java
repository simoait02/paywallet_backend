package com.paylogic.paywalletlite.domain.token;

import com.paylogic.paywalletlite.domain.wallet.Wallet;
import javax.persistence.*;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private Token token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_wallet_id", nullable = false)
    private Wallet payerWallet;

    @Column(name = "payer_wallet_id", insertable = false, updatable = false)
    private UUID payerWalletId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payee_wallet_id", nullable = false)
    private Wallet payeeWallet;

    @Column(name = "payee_wallet_id", insertable = false, updatable = false)
    private UUID payeeWalletId;

    @Column(name = "transferred_amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal transferredAmount;

    @Column(name = "transfer_timestamp", nullable = false)
    private LocalDateTime transferTimestamp;

    @Column(name = "payer_signature", nullable = false, length = 512)
    private String payerSignature;

    @Column(name = "payer_certificate", length = 2048)
    private String payerCertificate;

    @Column(name = "transfer_hash", nullable = false, length = 255)
    private String transferHash;

    public TokenTransferNode() {
        this.transferTimestamp = LocalDateTime.now();
    }

    // Getters & Setters
    public UUID getTransferNodeId() { return transferNodeId; }
    public void setTransferNodeId(UUID transferNodeId) { this.transferNodeId = transferNodeId; }

    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }

    public Wallet getPayerWallet() { return payerWallet; }
    public void setPayerWallet(Wallet payerWallet) {
        this.payerWallet = payerWallet;
        if (payerWallet != null) {
            this.payerWalletId = payerWallet.getWalletId();
        }
    }

    public UUID getPayerWalletId() {
        return payerWalletId != null ? payerWalletId :
                (payerWallet != null ? payerWallet.getWalletId() : null);
    }

    public Wallet getPayeeWallet() { return payeeWallet; }
    public void setPayeeWallet(Wallet payeeWallet) {
        this.payeeWallet = payeeWallet;
        if (payeeWallet != null) {
            this.payeeWalletId = payeeWallet.getWalletId();
        }
    }

    public UUID getPayeeWalletId() {
        return payeeWalletId != null ? payeeWalletId :
                (payeeWallet != null ? payeeWallet.getWalletId() : null);
    }

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