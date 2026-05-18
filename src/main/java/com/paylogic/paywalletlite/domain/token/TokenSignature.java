package com.paylogic.paywalletlite.domain.token;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "token_signatures", schema = "pwl_app")
public class TokenSignature {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "signature_id", updatable = false, nullable = false)
    private UUID signatureId;

    @Column(name = "token_id", nullable = false)
    private UUID tokenId;

    @Column(name = "signature_algorithm", nullable = false, length = 50)
    private String signatureAlgorithm;

    @Column(name = "signature_value", nullable = false, length = 4000)
    private String signatureValue;

    @Column(name = "issuer_public_key", nullable = false, length = 4000)
    private String issuerPublicKey;

    @Column(name = "signed_at", nullable = false)
    private LocalDateTime signedAt;

    @Column(name = "signed_data_hash", nullable = false, length = 255)
    private String signedDataHash;

    public TokenSignature() {
        this.signedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getSignatureId() { return signatureId; }
    public void setSignatureId(UUID signatureId) { this.signatureId = signatureId; }

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public String getSignatureAlgorithm() { return signatureAlgorithm; }
    public void setSignatureAlgorithm(String signatureAlgorithm) { this.signatureAlgorithm = signatureAlgorithm; }

    public String getSignatureValue() { return signatureValue; }
    public void setSignatureValue(String signatureValue) { this.signatureValue = signatureValue; }

    public String getIssuerPublicKey() { return issuerPublicKey; }
    public void setIssuerPublicKey(String issuerPublicKey) { this.issuerPublicKey = issuerPublicKey; }

    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }

    public String getSignedDataHash() { return signedDataHash; }
    public void setSignedDataHash(String signedDataHash) { this.signedDataHash = signedDataHash; }
}