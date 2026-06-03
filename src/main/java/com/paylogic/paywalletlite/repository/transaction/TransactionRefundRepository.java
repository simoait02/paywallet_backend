package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.TransactionRefund;
import com.paylogic.paywalletlite.domain.transaction.enums.RefundStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour les remboursements de surpaiement.
 */
public interface TransactionRefundRepository {

    TransactionRefund save(TransactionRefund refund);

    Optional<TransactionRefund> findById(UUID refundId);

    List<TransactionRefund> findByOriginalTransactionId(UUID originalTransactionId);

    List<TransactionRefund> findByWalletId(UUID walletId);

    List<TransactionRefund> findByStatus(RefundStatus status);

    Optional<TransactionRefund> findPendingByTransactionId(UUID transactionId);

    void updateStatus(UUID refundId, RefundStatus newStatus);
}