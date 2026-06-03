package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.Certificate;
import com.paylogic.paywalletlite.domain.crypto.enums.CertificateStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository {
    Certificate save(Certificate certificate);
    Optional<Certificate> findById(UUID certificateId);
    Optional<Certificate> findByThumbprint(String thumbprint);
    List<Certificate> findByWalletId(UUID walletId);
    List<Certificate> findByStatus(CertificateStatus status);
    List<Certificate> findByIssuerCaId(UUID caId);
    List<Certificate> findExpiringCertificates(LocalDateTime threshold);
    boolean existsByThumbprint(String thumbprint);
    void updateStatus(UUID certificateId, CertificateStatus status);
    void delete(Certificate certificate);
}