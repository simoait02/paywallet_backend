package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.TransactionRefund;
import com.paylogic.paywalletlite.domain.transaction.enums.RefundStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation JPA du repository TransactionRefund.
 */
@Repository
@Transactional(readOnly = true)
public class TransactionRefundRepositoryImpl implements TransactionRefundRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TransactionRefund save(TransactionRefund refund) {
        if (refund.getRefundId() == null) {
            entityManager.persist(refund);
            return refund;
        }
        return entityManager.merge(refund);
    }

    @Override
    public Optional<TransactionRefund> findById(UUID refundId) {
        return Optional.ofNullable(entityManager.find(TransactionRefund.class, refundId));
    }

    @Override
    public List<TransactionRefund> findByOriginalTransactionId(UUID originalTransactionId) {
        TypedQuery<TransactionRefund> query = entityManager.createQuery(
                "SELECT r FROM TransactionRefund r WHERE r.originalTransactionId = :txId ORDER BY r.processedAt DESC",
                TransactionRefund.class);
        query.setParameter("txId", originalTransactionId);
        return query.getResultList();
    }

    @Override
    public List<TransactionRefund> findByWalletId(UUID walletId) {
        TypedQuery<TransactionRefund> query = entityManager.createQuery(
                "SELECT r FROM TransactionRefund r WHERE r.walletId = :walletId ORDER BY r.processedAt DESC",
                TransactionRefund.class);
        query.setParameter("walletId", walletId);
        return query.getResultList();
    }

    @Override
    public List<TransactionRefund> findByStatus(RefundStatus status) {
        TypedQuery<TransactionRefund> query = entityManager.createQuery(
                "SELECT r FROM TransactionRefund r WHERE r.status = :status ORDER BY r.processedAt DESC",
                TransactionRefund.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public Optional<TransactionRefund> findPendingByTransactionId(UUID transactionId) {
        TypedQuery<TransactionRefund> query = entityManager.createQuery(
                "SELECT r FROM TransactionRefund r WHERE r.originalTransactionId = :txId AND r.status = :status",
                TransactionRefund.class);
        query.setParameter("txId", transactionId);
        query.setParameter("status", RefundStatus.PENDING);
        List<TransactionRefund> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    @Transactional
    public void updateStatus(UUID refundId, RefundStatus newStatus) {
        entityManager.createQuery(
                        "UPDATE TransactionRefund r SET r.status = :status WHERE r.refundId = :id")
                .setParameter("status", newStatus)
                .setParameter("id", refundId)
                .executeUpdate();
    }
}