package com.paylogic.paywalletlite.mapper;

import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.TransactionMetadata;
import com.paylogic.paywalletlite.dto.request.TransactionCreateRequestDto;
import com.paylogic.paywalletlite.dto.response.TransactionResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper manuel pour Transaction (Java 8 compatible, pas de MapStruct).
 * Convertit entre entité JPA et DTOs.
 */
@Component
public class TransactionMapper {

    /**
     * Convertit un DTO de création en entité Transaction.
     * Le statut initial est PENDING (online) ou OFFLINE_PENDING.
     */
    public Transaction toEntity(TransactionCreateRequestDto dto) {
        if (dto == null) return null;

        Transaction transaction = new Transaction();
        transaction.setSenderWalletId(dto.getSenderWalletId());
        transaction.setReceiverWalletId(dto.getReceiverWalletId());
        transaction.setRequestedAmount(dto.getRequestedAmount());
        transaction.setTransferredAmount(dto.getRequestedAmount()); // Initialement égal au montant demandé
        transaction.setType(dto.getType());
        transaction.setIsOffline(dto.getIsOffline() != null ? dto.getIsOffline() : false);
        transaction.setOfflineSignature(dto.getOfflineSignature());
        transaction.setInitiatedAt(LocalDateTime.now());
        transaction.setStatus(dto.getIsOffline() != null && dto.getIsOffline()
                ? com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus.OFFLINE_PENDING
                : com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus.PENDING);

        return transaction;
    }

    /**
     * Convertit une entité Transaction en DTO de réponse.
     */
    public TransactionResponseDto toResponseDto(Transaction entity) {
        if (entity == null) return null;

        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setTransactionId(entity.getTransactionId());
        dto.setSenderWalletId(entity.getSenderWalletId());
        dto.setReceiverWalletId(entity.getReceiverWalletId());
        dto.setRequestedAmount(entity.getRequestedAmount());
        dto.setTransferredAmount(entity.getTransferredAmount());
        dto.setOverpaymentAmount(entity.getOverpaymentAmount());
        dto.setOverpaymentStatus(entity.getOverpaymentStatus());
        dto.setType(entity.getType());
        dto.setStatus(entity.getStatus());
        dto.setInitiatedAt(entity.getInitiatedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setIsOffline(entity.getIsOffline());
        dto.setSyncBatchId(entity.getSyncBatchId());
        dto.setTransactionHash(entity.getTransactionHash());

        return dto;
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     */
    public void updateEntityFromDto(Transaction entity, TransactionCreateRequestDto dto) {
        if (entity == null || dto == null) return;

        if (dto.getRequestedAmount() != null) {
            entity.setTransferredAmount(dto.getRequestedAmount());
        }
        if (dto.getOfflineSignature() != null) {
            entity.setOfflineSignature(dto.getOfflineSignature());
        }
    }
}