package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;
import com.paylogic.paywalletlite.dto.request.TransactionCreateRequestDto;
import com.paylogic.paywalletlite.dto.response.TransactionResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour la gestion des transactions.
 * Couvre les 3 phases du workflow : préparation, paiement, reconciliation.
 */
public interface TransactionService {

    /**
     * Phase 1/2: Crée une nouvelle transaction (online ou offline).
     */
    TransactionResponseDto createTransaction(TransactionCreateRequestDto request);

    /**
     * Récupère une transaction par son ID.
     */
    TransactionResponseDto getTransactionById(UUID transactionId);

    /**
     * Liste toutes les transactions d'un wallet (expéditeur ou destinataire).
     */
    List<TransactionResponseDto> getTransactionsByWalletId(UUID walletId);

    /**
     * Liste les transactions par statut.
     */
    List<TransactionResponseDto> getTransactionsByStatus(TransactionStatus status);

    /**
     * Met à jour le statut d'une transaction.
     */
    TransactionResponseDto updateTransactionStatus(UUID transactionId, TransactionStatus newStatus);

    /**
     * Finalise une transaction (après reconciliation).
     */
    TransactionResponseDto completeTransaction(UUID transactionId);

    /**
     * Annule une transaction en attente.
     */
    TransactionResponseDto cancelTransaction(UUID transactionId);

    /**
     * Associe une transaction à un lot de synchronisation.
     */
    void assignToSyncBatch(UUID transactionId, UUID syncBatchId);

    /**
     * Calcule le montant du surpaiement si transferredAmount > requestedAmount.
     */
    BigDecimal calculateOverpayment(UUID transactionId);
}