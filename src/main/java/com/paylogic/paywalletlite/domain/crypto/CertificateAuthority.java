package com.paylogic.paywalletlite.domain.crypto;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificate_authorities", schema = "pwl_app")
public class CertificateAuthority {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ca_id", updatable = false, nullable = false)
    private UUID caId;

    @Column(name = "ca_name", nullable = false, length = 100)
    private String caName;

    @Column(name = "public_key_pem", nullable = false, length = 4000)
    private String publicKeyPem;

    @Column(name = "ca_certificate_pem", nullable = false, length = 4000)
    private String caCertificatePem;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "key_algorithm", length = 50)
    private String keyAlgorithm;

    public CertificateAuthority() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getCaId() { return caId; }
    public void setCaId(UUID caId) { this.caId = caId; }

    public String getCaName() { return caName; }
    public void setCaName(String caName) { this.caName = caName; }

    public String getPublicKeyPem() { return publicKeyPem; }
    public void setPublicKeyPem(String publicKeyPem) { this.publicKeyPem = publicKeyPem; }

    public String getCaCertificatePem() { return caCertificatePem; }
    public void setCaCertificatePem(String caCertificatePem) { this.caCertificatePem = caCertificatePem; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getKeyAlgorithm() { return keyAlgorithm; }
    public void setKeyAlgorithm(String keyAlgorithm) { this.keyAlgorithm = keyAlgorithm; }
}