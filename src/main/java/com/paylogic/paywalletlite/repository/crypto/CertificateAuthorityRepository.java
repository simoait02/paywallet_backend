package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.CertificateAuthority;
import com.paylogic.paywalletlite.domain.crypto.enums.CAStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateAuthorityRepository {
    CertificateAuthority save(CertificateAuthority ca);
    Optional<CertificateAuthority> findById(UUID caId);
    Optional<CertificateAuthority> findActive();
    List<CertificateAuthority> findByStatus(CAStatus status);
    boolean existsByCaName(String caName);
    void updateStatus(UUID caId, CAStatus status);
    void delete(CertificateAuthority ca);
}