package com.paylogic.paywalletlite.domain.crypto;

import com.paylogic.paywalletlite.domain.crypto.enums.CertificateStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates", schema = "pwl_app")
public class Certificate {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "certificate_id", updatable = false, nullable = false)
    private UUID certificateId;

    // Relation JPA vers le wallet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(name = "wallet_id", insertable = false, updatable = false)
    private UUID walletId;

    @Column(name = "certificate_pem", nullable = false, length = 4000)
    private String certificatePem;

    @Column(name = "thumbprint", nullable = false, unique = true, length = 255)
    private String thumbprint;

    // Relation JPA vers la CA émettrice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_ca_id", nullable = false)
    private CertificateAuthority issuerCa;

    @Column(name = "issuer_ca_id", insertable = false, updatable = false)
    private UUID issuerCaId;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CertificateStatus status;

    @Column(name = "revocation_reason", length = 255)
    private String revocationReason;

    public Certificate() {
        this.issuedAt = LocalDateTime.now();
        this.status = CertificateStatus.PENDING;
    }

    // Getters & Setters
    public UUID getCertificateId() { return certificateId; }
    public void setCertificateId(UUID certificateId) { this.certificateId = certificateId; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
        if (wallet != null) {
            this.walletId = wallet.getWalletId();
        }
    }

    public UUID getWalletId() {
        return walletId != null ? walletId :
                (wallet != null ? wallet.getWalletId() : null);
    }

    public String getCertificatePem() { return certificatePem; }
    public void setCertificatePem(String certificatePem) { this.certificatePem = certificatePem; }

    public String getThumbprint() { return thumbprint; }
    public void setThumbprint(String thumbprint) { this.thumbprint = thumbprint; }

    public CertificateAuthority getIssuerCa() { return issuerCa; }
    public void setIssuerCa(CertificateAuthority issuerCa) {
        this.issuerCa = issuerCa;
        if (issuerCa != null) {
            this.issuerCaId = issuerCa.getCaId();
        }
    }

    public UUID getIssuerCaId() {
        return issuerCaId != null ? issuerCaId :
                (issuerCa != null ? issuerCa.getCaId() : null);
    }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

    public CertificateStatus getStatus() { return status; }
    public void setStatus(CertificateStatus status) { this.status = status; }

    public String getRevocationReason() { return revocationReason; }
    public void setRevocationReason(String revocationReason) { this.revocationReason = revocationReason; }

    // Méthodes métier
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isValid() {
        return status == CertificateStatus.VALID && !isExpired();
    }

    public void revoke(String reason) {
        this.status = CertificateStatus.REVOKED;
        this.revokedAt = LocalDateTime.now();
        this.revocationReason = reason;
    }
}