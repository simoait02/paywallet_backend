package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour l'entité Transaction.
 * Interface définissant les opérations d'accès aux données.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID transactionId);

    List<Transaction> findBySenderWalletId(UUID senderWalletId);

    List<Transaction> findByReceiverWalletId(UUID receiverWalletId);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByType(TransactionType type);

    List<Transaction> findBySyncBatchId(UUID syncBatchId);

    List<Transaction> findPendingTransactionsByWalletId(UUID walletId);

    List<Transaction> findOfflinePendingByWalletId(UUID walletId);

    long countByStatusAndType(TransactionStatus status, TransactionType type);

    void updateStatus(UUID transactionId, TransactionStatus newStatus);

    void updateSyncBatchId(UUID transactionId, UUID syncBatchId);
}