package com.paylogic.paywalletlite.dto.response;

import com.paylogic.paywalletlite.domain.crypto.enums.CertificateStatus;

import java.time.LocalDateTime;
import java.util.UUID;

// CertificateResponseDto.java
public class CertificateResponseDto {
    private UUID certificateId;
    private UUID walletId;
    private String certificatePem;
    private String thumbprint;
    private UUID issuerCaId;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private CertificateStatus status;


    public UUID getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(UUID certificateId) {
        this.certificateId = certificateId;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public String getCertificatePem() {
        return certificatePem;
    }

    public void setCertificatePem(String certificatePem) {
        this.certificatePem = certificatePem;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public void setThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public UUID getIssuerCaId() {
        return issuerCaId;
    }

    public void setIssuerCaId(UUID issuerCaId) {
        this.issuerCaId = issuerCaId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public CertificateStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateStatus status) {
        this.status = status;
    }
    // PAS de référence directe à Wallet ou User !
}