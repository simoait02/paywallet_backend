package com.paylogic.paywalletlite.mapper;

import com.paylogic.paywalletlite.domain.crypto.Certificate;
import com.paylogic.paywalletlite.dto.response.CertificateResponseDto;
import org.springframework.stereotype.Component;

// CertificateMapper.java
@Component
public class CertificateMapper {

    public CertificateResponseDto toDto(Certificate certificate) {
        CertificateResponseDto dto = new CertificateResponseDto();
        dto.setCertificateId(certificate.getCertificateId());
        dto.setWalletId(certificate.getWalletId());
        dto.setCertificatePem(certificate.getCertificatePem());
        dto.setThumbprint(certificate.getThumbprint());
        dto.setIssuerCaId(certificate.getIssuerCaId());
        dto.setIssuedAt(certificate.getIssuedAt());
        dto.setExpiresAt(certificate.getExpiresAt());
        dto.setStatus(certificate.getStatus());
        return dto;
    }
}
