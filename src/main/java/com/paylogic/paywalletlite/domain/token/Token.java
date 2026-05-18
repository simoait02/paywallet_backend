package com.paylogic.paywalletlite.domain.token;

import com.paylogic.paywalletlite.domain.token.enums.AllocationMode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens", schema = "pwl_app")
public class Token {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "token_id", updatable = false, nullable = false)
    private UUID tokenId;

    @Column(name = "value", precision = 19, scale = 2, nullable = false)
    private BigDecimal value;

    @Column(name = "issuer_id", nullable = false)
    private UUID issuerId;

    @Column(name = "issuer_wallet_id", nullable = false)
    private UUID issuerWalletId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "nonce", nullable = false, unique = true, length = 255)
    private String nonce;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TokenStatus status;

    @Column(name = "current_holder_wallet_id")
    private UUID currentHolderWalletId;

    @Column(name = "original_wallet_id", nullable = false)
    private UUID originalWalletId;

    @Enumerated(EnumType.STRING)
    @Column(name = "allocation_mode", nullable = false, length = 30)
    private AllocationMode allocationMode;

    @Column(name = "transfer_count")
    private Integer transferCount;

    @Column(name = "max_transfers")
    private Integer maxTransfers;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "last_transfer_at")
    private LocalDateTime lastTransferAt;

    public Token() {
        this.issuedAt = LocalDateTime.now();
        this.status = TokenStatus.CREATED;
        this.transferCount = 0;
    }

    // Getters et Setters
    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public UUID getIssuerId() { return issuerId; }
    public void setIssuerId(UUID issuerId) { this.issuerId = issuerId; }

    public UUID getIssuerWalletId() { return issuerWalletId; }
    public void setIssuerWalletId(UUID issuerWalletId) { this.issuerWalletId = issuerWalletId; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }

    public UUID getCurrentHolderWalletId() { return currentHolderWalletId; }
    public void setCurrentHolderWalletId(UUID currentHolderWalletId) { this.currentHolderWalletId = currentHolderWalletId; }

    public UUID getOriginalWalletId() { return originalWalletId; }
    public void setOriginalWalletId(UUID originalWalletId) { this.originalWalletId = originalWalletId; }

    public AllocationMode getAllocationMode() { return allocationMode; }
    public void setAllocationMode(AllocationMode allocationMode) { this.allocationMode = allocationMode; }

    public Integer getTransferCount() { return transferCount; }
    public void setTransferCount(Integer transferCount) { this.transferCount = transferCount; }

    public Integer getMaxTransfers() { return maxTransfers; }
    public void setMaxTransfers(Integer maxTransfers) { this.maxTransfers = maxTransfers; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public LocalDateTime getLastTransferAt() { return lastTransferAt; }
    public void setLastTransferAt(LocalDateTime lastTransferAt) { this.lastTransferAt = lastTransferAt; }
}