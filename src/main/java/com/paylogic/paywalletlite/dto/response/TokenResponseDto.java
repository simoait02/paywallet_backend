package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TokenResponseDto {

    private UUID tokenId;
    private BigDecimal value;
    private TokenStatus status;
    private String nonce;
    private String tokenHash;
    private String tokenSignature;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private Integer transferCount;
    private Integer maxTransfers;
    private UUID holderWalletId;
    private UUID originalWalletId;
    private UUID issuerId;
    private UUID issuerWalletId;
    private String allocationMode;

    public TokenResponseDto() {}

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public TokenStatus getStatus() { return status; }
    public void setStatus(TokenStatus status) { this.status = status; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public Integer getTransferCount() { return transferCount; }
    public void setTransferCount(Integer transferCount) { this.transferCount = transferCount; }

    public Integer getMaxTransfers() { return maxTransfers; }
    public void setMaxTransfers(Integer maxTransfers) { this.maxTransfers = maxTransfers; }

    public UUID getHolderWalletId() { return holderWalletId; }
    public void setHolderWalletId(UUID holderWalletId) { this.holderWalletId = holderWalletId; }

    public UUID getOriginalWalletId() { return originalWalletId; }
    public void setOriginalWalletId(UUID originalWalletId) { this.originalWalletId = originalWalletId; }

    public UUID getIssuerWalletId() { return issuerWalletId; }
    public void setIssuerWalletId(UUID issuerWalletId) { this.issuerWalletId = issuerWalletId; }

    public UUID getIssuerId() { return issuerId; }
    public void setIssuerId(UUID issuerId) { this.issuerId = issuerId; }

    public String getAllocationMode() { return allocationMode; }
    public void setAllocationMode(String allocationMode) { this.allocationMode = allocationMode; }

    public String getTokenSignature() {return tokenSignature;}
    public void setTokenSignature(String tokenSignature) {this.tokenSignature = tokenSignature;}

}