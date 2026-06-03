package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.transaction.enums.ReconciliationStatus;
import com.paylogic.paywalletlite.dto.request.SyncRequestDto;
import com.paylogic.paywalletlite.dto.response.SyncResponseDto;
import com.paylogic.paywalletlite.dto.response.TokenRedemptionResultDto;

import java.util.List;
import java.util.UUID;

/**
 * Service de réconciliation (Phase 3).
 * Valide et finalise les transactions offline via synchronisation différée.
 * Gère la détection de double-dépense et le rachat de tokens.
 */
public interface ReconciliationService {

    /**
     * Lance un processus de synchronisation pour un wallet.
     * Le receiver soumet ses tokens reçus pour validation.
     */
    SyncResponseDto synchronize(SyncRequestDto request);

    /**
     * Valide un token individuel lors de la reconciliation.
     * Vérifie signature, expiration, et absence de double-dépense.

    TokenRedemptionResultDto validateAndRedeemToken(UUID tokenId, UUID receiverWalletId);
    */

    /**
     * Détecte les tentatives de double-dépense.

    boolean detectDoubleSpend(UUID tokenId);
    */

    /**
     * Récupère le statut d'une reconciliation.
     */
    ReconciliationStatus getReconciliationStatus(UUID batchId);

    /**
     * Génère le rapport de discrepancy (écart) pour un lot de sync.
     */
    String generateDiscrepancyReport(UUID batchId);

    /**
     * Finalise le settlement : crédite le receiver, débite le sender.
     */
    void executeSettlement(UUID transactionId, UUID tokenId);

    /**
     * Annule une reconciliation en cours.
     */
    void cancelReconciliation(UUID batchId);
}