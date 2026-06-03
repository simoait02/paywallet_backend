package com.paylogic.paywalletlite.domain.crypto;

import com.paylogic.paywalletlite.domain.crypto.enums.CAStatus;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CAStatus status;

    // Relation vers les certificats émis par cette CA
    @OneToMany(mappedBy = "issuerCa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Certificate> issuedCertificates = new ArrayList<>();

    public CertificateAuthority() {
        this.createdAt = LocalDateTime.now();
        this.status = CAStatus.ACTIVE;
    }

    // Getters & Setters
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

    public CAStatus getStatus() { return status; }
    public void setStatus(CAStatus status) { this.status = status; }

    public List<Certificate> getIssuedCertificates() { return issuedCertificates; }
    public void setIssuedCertificates(List<Certificate> issuedCertificates) { this.issuedCertificates = issuedCertificates; }

    // Méthodes métier
    public boolean isActive() {
        return status == CAStatus.ACTIVE;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }
}