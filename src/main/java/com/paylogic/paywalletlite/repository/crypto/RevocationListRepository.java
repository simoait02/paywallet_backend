package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.RevocationList;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RevocationListRepository {
    RevocationList save(RevocationList entry);
    Optional<RevocationList> findById(UUID entryId);
    Optional<RevocationList> findByCertificateId(UUID certificateId);
    List<RevocationList> findAll();
    List<RevocationList> findByReason(String reason);
    boolean existsByCertificateId(UUID certificateId);
    void delete(RevocationList entry);
}