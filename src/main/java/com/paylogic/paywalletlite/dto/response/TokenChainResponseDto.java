package com.paylogic.paywalletlite.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenChainResponseDto {

    private UUID tokenId;
    private BigDecimal tokenValue;
    private String tokenStatus;
    private Integer transferCount;
    private Integer maxTransfers;
    private List<ChainLinkDto> chain = new ArrayList<>();

    public TokenChainResponseDto() {}

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public BigDecimal getTokenValue() { return tokenValue; }
    public void setTokenValue(BigDecimal tokenValue) { this.tokenValue = tokenValue; }

    public String getTokenStatus() { return tokenStatus; }
    public void setTokenStatus(String tokenStatus) { this.tokenStatus = tokenStatus; }

    public Integer getTransferCount() { return transferCount; }
    public void setTransferCount(Integer transferCount) { this.transferCount = transferCount; }

    public Integer getMaxTransfers() { return maxTransfers; }
    public void setMaxTransfers(Integer maxTransfers) { this.maxTransfers = maxTransfers; }

    public List<ChainLinkDto> getChain() { return chain; }
    public void setChain(List<ChainLinkDto> chain) { this.chain = chain; }

    // Classe interne pour un maillon de la chaîne
    public static class ChainLinkDto {
        private Integer step;
        private String action; // ISSUANCE, TRANSFER, REDEMPTION
        private UUID fromWalletId;
        private String fromWalletOwner;
        private UUID toWalletId;
        private String toWalletOwner;
        private BigDecimal amount;
        private LocalDateTime timestamp;
        private String signature;
        private String transferHash;

        public Integer getStep() { return step; }
        public void setStep(Integer step) { this.step = step; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }

        public UUID getFromWalletId() { return fromWalletId; }
        public void setFromWalletId(UUID fromWalletId) { this.fromWalletId = fromWalletId; }

        public String getFromWalletOwner() { return fromWalletOwner; }
        public void setFromWalletOwner(String fromWalletOwner) { this.fromWalletOwner = fromWalletOwner; }

        public UUID getToWalletId() { return toWalletId; }
        public void setToWalletId(UUID toWalletId) { this.toWalletId = toWalletId; }

        public String getToWalletOwner() { return toWalletOwner; }
        public void setToWalletOwner(String toWalletOwner) { this.toWalletOwner = toWalletOwner; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public String getSignature() { return signature; }
        public void setSignature(String signature) { this.signature = signature; }

        public String getTransferHash() { return transferHash; }
        public void setTransferHash(String transferHash) { this.transferHash = transferHash; }
    }
}