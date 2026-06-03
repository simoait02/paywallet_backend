package com.paylogic.paywalletlite.domain.token;

import javax.persistence.*;

import com.paylogic.paywalletlite.domain.token.enums.OfflineTokenValidationStatus;
import org.hibernate.annotations.GenericGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité de liaison entre une Transaction offline et les Tokens impliqués.
 *
 * Stocke les informations reçues du device pour :
 * - Référencer le token original sans le recréer
 * - Conserver les données de transfert pour validation
 * - Permettre la comparaison avec le token original lors de la redemption
 * - Tracer les tokens utilisés dans chaque transaction offline
 *
 * Cette entité est temporaire : une fois la transaction traitée (COMPLETED),
 * les données validées sont dans Token et TokenTransferNode.
 */
@Entity
@Table(name = "offline_transaction_tokens", schema = "pwl_app")
public class OfflineTransactionToken {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Transaction offline associée */
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    /** ID du token original (déjà existant en base ou à valider) */
    @Column(name = "token_id", nullable = false)
    private UUID tokenId;

    /** ID du propriétaire initial */
    @Column(name = "issuer_id", nullable = false)
    private UUID issuerId;

    /** Valeur du token au moment du transfert */
    @Column(name = "token_value", precision = 19, scale = 2, nullable = false)
    private BigDecimal tokenValue;

    /** Nonce du token (pour vérification anti-rejeu) */
    @Column(name = "token_nonce", nullable = false)
    private String tokenNonce;

    /** Hash du token reçu du device (pour vérification d'intégrité) */
    @Column(name = "token_hash")
    private String tokenHash;

    /** Signature du backend sur le token (reçue du device) */
    @Column(name = "backend_signature", length = 4000)
    private String backendSignature;

    /** Date d'émission du token */
    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    /** Date d'expiration du token */
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /** Mode d'allocation (EXPLICIT, CONDITIONAL, CREDIT) */
    @Column(name = "allocation_mode", length = 30)
    private String allocationMode;

    /** Wallet original (premier propriétaire) */
    @Column(name = "original_wallet_id")
    private UUID originalWalletId;

    /** Wallet payeur dans cette transaction */
    @Column(name = "payer_wallet_id")
    private UUID payerWalletId;

    /** Wallet bénéficiaire dans cette transaction */
    @Column(name = "payee_wallet_id")
    private UUID payeeWalletId;

    /** Montant transféré dans cette transaction */
    @Column(name = "transferred_amount", precision = 19, scale = 2)
    private BigDecimal transferredAmount;

    /** Timestamp du transfert (heure device) */
    @Column(name = "transfer_timestamp")
    private LocalDateTime transferTimestamp;

    /** Signature du payeur sur le transfert */
    @Column(name = "payer_signature", length = 4000)
    private String payerSignature;

    /** Certificat du payeur */
    @Column(name = "payer_certificate", length = 4000)
    private String payerCertificate;

    /** Hash du transfert */
    @Column(name = "transfer_hash")
    private String transferHash;

    /** Numéro de séquence dans la chaîne de transfert */
    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    /** Chaîne de transfert complète (JSON) pour validation */
    @Column(name = "transfer_chain_json", columnDefinition = "CLOB")
    private String transferChainJson;

    /** Statut de validation de ce token dans la transaction */
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", length = 50)
    private OfflineTokenValidationStatus validationStatus;

    /** Date de création */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public OfflineTransactionToken() {
        this.createdAt = LocalDateTime.now();
        this.validationStatus = OfflineTokenValidationStatus.PENDING_VALIDATION;
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public UUID getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(UUID issuerId) {
        this.issuerId = issuerId;
    }

    public BigDecimal getTokenValue() { return tokenValue; }
    public void setTokenValue(BigDecimal tokenValue) { this.tokenValue = tokenValue; }

    public String getTokenNonce() { return tokenNonce; }
    public void setTokenNonce(String tokenNonce) { this.tokenNonce = tokenNonce; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public String getBackendSignature() { return backendSignature; }
    public void setBackendSignature(String backendSignature) { this.backendSignature = backendSignature; }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getAllocationMode() { return allocationMode; }
    public void setAllocationMode(String allocationMode) { this.allocationMode = allocationMode; }

    public UUID getOriginalWalletId() { return originalWalletId; }
    public void setOriginalWalletId(UUID originalWalletId) { this.originalWalletId = originalWalletId; }

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

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public String getTransferChainJson() { return transferChainJson; }
    public void setTransferChainJson(String transferChainJson) { this.transferChainJson = transferChainJson; }

    public OfflineTokenValidationStatus getValidationStatus() { return validationStatus; }
    public void setValidationStatus(OfflineTokenValidationStatus validationStatus) { this.validationStatus = validationStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}