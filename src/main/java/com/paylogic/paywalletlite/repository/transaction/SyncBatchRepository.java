package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.SyncBatch;
import com.paylogic.paywalletlite.domain.transaction.enums.SyncBatchStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour les lots de synchronisation (SyncBatch).
 */
public interface SyncBatchRepository {

    SyncBatch save(SyncBatch syncBatch);

    Optional<SyncBatch> findById(UUID batchId);

    List<SyncBatch> findByWalletId(UUID walletId);

    List<SyncBatch> findByStatus(SyncBatchStatus status);

    List<SyncBatch> findByDeviceId(UUID deviceId);

    Optional<SyncBatch> findLatestByWalletId(UUID walletId);

    void updateStatus(UUID batchId, SyncBatchStatus newStatus);

    void updateDiscrepancy(UUID batchId, java.math.BigDecimal discrepancy);
}