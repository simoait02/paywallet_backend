package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation JPA du repository Transaction.
 * Schéma: pwl_app
 */
@Repository
@Transactional(readOnly = true)
public class TransactionRepositoryImpl implements TransactionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        if (transaction.getTransactionId() == null) {
            entityManager.persist(transaction);
            return transaction;
        }
        return entityManager.merge(transaction);
    }

    @Override
    public Optional<Transaction> findById(UUID transactionId) {
        Transaction transaction = entityManager.find(Transaction.class, transactionId);
        return Optional.ofNullable(transaction);
    }

    @Override
    public List<Transaction> findBySenderWalletId(UUID senderWalletId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.senderWalletId = :senderId ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("senderId", senderWalletId);
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByReceiverWalletId(UUID receiverWalletId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.receiverWalletId = :receiverId ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("receiverId", receiverWalletId);
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.status = :status ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByType(TransactionType type) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.type = :type ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    @Override
    public List<Transaction> findBySyncBatchId(UUID syncBatchId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.syncBatchId = :batchId ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("batchId", syncBatchId);
        return query.getResultList();
    }

    @Override
    public List<Transaction> findPendingTransactionsByWalletId(UUID walletId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE (t.senderWalletId = :walletId OR t.receiverWalletId = :walletId) " +
                        "AND t.status IN (:pendingStatuses) ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("walletId", walletId);
        query.setParameter("pendingStatuses", java.util.Arrays.asList(
                TransactionStatus.PENDING,
                TransactionStatus.OFFLINE_PENDING
        ));
        return query.getResultList();
    }

    @Override
    public List<Transaction> findOfflinePendingByWalletId(UUID walletId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.receiverWalletId = :walletId " +
                        "AND t.status = :status ORDER BY t.initiatedAt DESC",
                Transaction.class);
        query.setParameter("walletId", walletId);
        query.setParameter("status", TransactionStatus.OFFLINE_PENDING);
        return query.getResultList();
    }

    @Override
    public long countByStatusAndType(TransactionStatus status, TransactionType type) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(t) FROM Transaction t WHERE t.status = :status AND t.type = :type",
                Long.class);
        query.setParameter("status", status);
        query.setParameter("type", type);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void updateStatus(UUID transactionId, TransactionStatus newStatus) {
        entityManager.createQuery(
                        "UPDATE Transaction t SET t.status = :status WHERE t.transactionId = :id")
                .setParameter("status", newStatus)
                .setParameter("id", transactionId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateSyncBatchId(UUID transactionId, UUID syncBatchId) {
        entityManager.createQuery(
                        "UPDATE Transaction t SET t.syncBatchId = :batchId WHERE t.transactionId = :id")
                .setParameter("batchId", syncBatchId)
                .setParameter("id", transactionId)
                .executeUpdate();
    }
}