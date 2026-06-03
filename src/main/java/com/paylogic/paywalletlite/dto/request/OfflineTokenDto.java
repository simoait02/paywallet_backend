package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.token.enums.OfflineTokenValidationStatus;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO représentant un token offline avec sa chaîne de transfert complète.
 *
 * La chaîne de transfert (transferNodes) permet au backend de :
 * - Vérifier la légitimité du dernier porteur
 * - Détecter les doubles dépenses
 * - Reconstituer l'historique complet du token
 */
public class OfflineTokenDto {

    /** ID du token (généré par le backend lors de l'allocation) */
    @NotNull(message = "Token ID is required")
    private UUID tokenId;

    /** ID du propriétaire initial */
    @NotNull(message = "Issuer ID is required")
    private UUID issuerId;

    /** Valeur du token */
    @NotNull(message = "Token value is required")
    private BigDecimal value;

    /** Le statut actuel du token */
    @NotNull(message = "Token status is required")
    private OfflineTokenValidationStatus validationStatus;

    /** Nonce unique pour détection anti-rejeu */
    @NotNull(message = "Token nonce is required")
    private String nonce;

    /** Signature du backend sur le token (preuve d'émission) */
    @NotNull(message = "Backend signature is required")
    private String backendSignature;

    /** Hash du token pour vérification d'intégrité */
    @NotNull(message = "Token hash is required")
    private String tokenHash;

    /** Date d'émission du token */
    @NotNull(message = "Issued date is required")
    private String issuedAt;

    /** Date d'expiration du token */
    @NotNull(message = "Expiration date is required")
    private String expiresAt;

    /** Mode d'allocation (EXPLICIT, CONDITIONAL, CREDIT) */
    @NotNull(message = "Allocation mode is required")
    private String allocationMode;

    /** Wallet original (premier propriétaire) */
    @NotNull(message = "Original wallet ID is required")
    private UUID originalWalletId;

    /**
     * Chaîne de transfert complète (liste chaînée).
     * Chaque nœud représente un transfert de propriété.
     * Le dernier nœud désigne le propriétaire actuel.
     */
    @NotEmpty(message = "Transfer chain is required")
    @Valid
    private List<TransferNodeDto> transferNodes;

    public OfflineTokenDto() {}

    // Getters et Setters
    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }

    public String getBackendSignature() { return backendSignature; }
    public void setBackendSignature(String backendSignature) { this.backendSignature = backendSignature; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public String getAllocationMode() { return allocationMode; }
    public void setAllocationMode(String allocationMode) { this.allocationMode = allocationMode; }

    public UUID getOriginalWalletId() { return originalWalletId; }
    public void setOriginalWalletId(UUID originalWalletId) { this.originalWalletId = originalWalletId; }

    public List<TransferNodeDto> getTransferNodes() { return transferNodes; }
    public void setTransferNodes(List<TransferNodeDto> transferNodes) { this.transferNodes = transferNodes; }

    public UUID getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(UUID issuerId) {
        this.issuerId = issuerId;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public OfflineTokenValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(OfflineTokenValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }
}