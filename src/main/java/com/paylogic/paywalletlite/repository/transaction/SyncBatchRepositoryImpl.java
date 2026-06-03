package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.SyncBatch;
import com.paylogic.paywalletlite.domain.transaction.enums.SyncBatchStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation JPA du repository SyncBatch.
 */
@Repository
@Transactional(readOnly = true)
public class SyncBatchRepositoryImpl implements SyncBatchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public SyncBatch save(SyncBatch syncBatch) {
        if (syncBatch.getBatchId() == null) {
            entityManager.persist(syncBatch);
            return syncBatch;
        }
        return entityManager.merge(syncBatch);
    }

    @Override
    public Optional<SyncBatch> findById(UUID batchId) {
        return Optional.ofNullable(entityManager.find(SyncBatch.class, batchId));
    }

    @Override
    public List<SyncBatch> findByWalletId(UUID walletId) {
        TypedQuery<SyncBatch> query = entityManager.createQuery(
                "SELECT s FROM SyncBatch s WHERE s.walletId = :walletId ORDER BY s.startedAt DESC",
                SyncBatch.class);
        query.setParameter("walletId", walletId);
        return query.getResultList();
    }

    @Override
    public List<SyncBatch> findByStatus(SyncBatchStatus status) {
        TypedQuery<SyncBatch> query = entityManager.createQuery(
                "SELECT s FROM SyncBatch s WHERE s.status = :status ORDER BY s.startedAt DESC",
                SyncBatch.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<SyncBatch> findByDeviceId(UUID deviceId) {
        TypedQuery<SyncBatch> query = entityManager.createQuery(
                "SELECT s FROM SyncBatch s WHERE s.deviceId = :deviceId ORDER BY s.startedAt DESC",
                SyncBatch.class);
        query.setParameter("deviceId", deviceId);
        return query.getResultList();
    }

    @Override
    public Optional<SyncBatch> findLatestByWalletId(UUID walletId) {
        TypedQuery<SyncBatch> query = entityManager.createQuery(
                "SELECT s FROM SyncBatch s WHERE s.walletId = :walletId ORDER BY s.startedAt DESC",
                SyncBatch.class);
        query.setParameter("walletId", walletId);
        query.setMaxResults(1);
        List<SyncBatch> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    @Transactional
    public void updateStatus(UUID batchId, SyncBatchStatus newStatus) {
        entityManager.createQuery(
                        "UPDATE SyncBatch s SET s.status = :status WHERE s.batchId = :id")
                .setParameter("status", newStatus)
                .setParameter("id", batchId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateDiscrepancy(UUID batchId, BigDecimal discrepancy) {
        entityManager.createQuery(
                        "UPDATE SyncBatch s SET s.discrepancy = :discrepancy WHERE s.batchId = :id")
                .setParameter("discrepancy", discrepancy)
                .setParameter("id", batchId)
                .executeUpdate();
    }
}