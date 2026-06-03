package com.paylogic.paywalletlite.domain.token;

import com.paylogic.paywalletlite.domain.token.enums.AllocationMode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    // Relation JPA vers le wallet émetteur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_wallet_id", nullable = false)
    private Wallet issuerWallet;

    // Colonne brute pour usage offline (quand la relation n'est pas chargée)
    @Column(name = "issuer_wallet_id", insertable = false, updatable = false)
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

    // Relation JPA vers le détenteur actuel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_holder_wallet_id")
    private Wallet currentHolderWallet;

    // Colonne brute pour usage offline
    @Column(name = "current_holder_wallet_id", insertable = false, updatable = false)
    private UUID currentHolderWalletId;

    // Relation JPA vers le wallet propriétaire original
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_wallet_id", nullable = false)
    private Wallet originalWallet;

    // Colonne brute pour usage offline
    @Column(name = "original_wallet_id", insertable = false, updatable = false)
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

    // Relation vers la signature cryptographique
    @OneToOne(mappedBy = "token", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private TokenSignature tokenSignature;

    // Historique des transferts (chaîne de possession)
    @OneToMany(mappedBy = "token", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("transferTimestamp DESC")
    private List<TokenTransferNode> transferHistory = new ArrayList<>();

    public Token() {
        this.issuedAt = LocalDateTime.now();
        this.status = TokenStatus.CREATED;
        this.transferCount = 0;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public UUID getIssuerId() { return issuerId; }
    public void setIssuerId(UUID issuerId) { this.issuerId = issuerId; }

    public Wallet getIssuerWallet() { return issuerWallet; }
    public void setIssuerWallet(Wallet issuerWallet) {
        this.issuerWallet = issuerWallet;
        if (issuerWallet != null) {
            this.issuerWalletId = issuerWallet.getWalletId();
        }
    }

    public UUID getIssuerWalletId() {
        return issuerWalletId != null ? issuerWalletId :
                (issuerWallet != null ? issuerWallet.getWalletId() : null);
    }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }

    public Wallet getCurrentHolderWallet() { return currentHolderWallet; }
    public void setCurrentHolderWallet(Wallet currentHolderWallet) {
        this.currentHolderWallet = currentHolderWallet;
        if (currentHolderWallet != null) {
            this.currentHolderWalletId = currentHolderWallet.getWalletId();
        }
    }

    public UUID getCurrentHolderWalletId() {
        return currentHolderWalletId != null ? currentHolderWalletId :
                (currentHolderWallet != null ? currentHolderWallet.getWalletId() : null);
    }

    public Wallet getOriginalWallet() { return originalWallet; }
    public void setOriginalWallet(Wallet originalWallet) {
        this.originalWallet = originalWallet;
        if (originalWallet != null) {
            this.originalWalletId = originalWallet.getWalletId();
        }
    }

    public UUID getOriginalWalletId() {
        return originalWalletId != null ? originalWalletId :
                (originalWallet != null ? originalWallet.getWalletId() : null);
    }

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

    public TokenSignature getTokenSignature() { return tokenSignature; }
    public void setTokenSignature(TokenSignature tokenSignature) { this.tokenSignature = tokenSignature; }

    public List<TokenTransferNode> getTransferHistory() { return transferHistory; }
    public void setTransferHistory(List<TokenTransferNode> transferHistory) { this.transferHistory = transferHistory; }

    // ============================================================
    // MÉTHODES MÉTIER
    // ============================================================

    /**
     * Vérifie si le token peut être transféré.
     */
    public boolean isTransferable() {
        if (status != TokenStatus.ALLOCATED && status != TokenStatus.TRANSFERRED) {
            return false;
        }
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            return false;
        }
        if (transferCount != null && maxTransfers != null && transferCount >= maxTransfers) {
            return false;
        }
        return true;
    }

    /**
     * Vérifie si le token peut être redeemé.
     */
    public boolean isRedeemable() {
        if (status == TokenStatus.REDEEMED || status == TokenStatus.INVALID || status == TokenStatus.REVOKED) {
            return false;
        }
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    /**
     * Incrémente le compteur de transferts.
     */
    public void incrementTransferCount() {
        if (this.transferCount == null) {
            this.transferCount = 0;
        }
        this.transferCount++;
        this.lastTransferAt = LocalDateTime.now();
    }

    /**
     * Marque le token comme redeemé.
     */
    public void markAsRedeemed() {
        this.status = TokenStatus.REDEEMED;
    }

    /**
     * Marque le token comme expiré.
     */
    public void markAsExpired() {
        this.status = TokenStatus.EXPIRED;
    }

    /**
     * Marque le token comme révoqué.
     */
    public void markAsRevoked() {
        this.status = TokenStatus.REVOKED;
    }

    @Override
    public String toString() {
        return String.format("Token[%s, value=%s, status=%s, nonce=%s]",
                tokenId, value, status, nonce);
    }
}