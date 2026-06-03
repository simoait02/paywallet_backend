package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServerKeyRepository {
    ServerKey save(ServerKey serverKey);
    Optional<ServerKey> findById(UUID serverKeyId);
    Optional<ServerKey> findActiveByPurpose(ServerKeyPurpose purpose);
    List<ServerKey> findByStatus(ServerKeyStatus status);
    List<ServerKey> findByPurpose(ServerKeyPurpose purpose);
    List<ServerKey> findExpiringKeys(LocalDateTime threshold);
    Optional<ServerKey> findByWalletIdAndPurpose(UUID walletId, ServerKeyPurpose purpose);
    boolean existsByPurposeAndStatus(ServerKeyPurpose purpose, ServerKeyStatus status);
    void updateStatus(UUID serverKeyId, ServerKeyStatus status);
    void updateRotatedAt(UUID serverKeyId, LocalDateTime rotatedAt);
    void delete(ServerKey serverKey);
}