package com.paylogic.paywalletlite.service.sync;

import com.paylogic.paywalletlite.domain.transaction.SyncBatch;
import com.paylogic.paywalletlite.domain.transaction.enums.SyncBatchStatus;
import com.paylogic.paywalletlite.dto.request.SyncRequestDto;
import com.paylogic.paywalletlite.dto.response.SyncResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.transaction.SyncBatchRepository;
import com.paylogic.paywalletlite.service.transaction.ReconciliationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implémentation du service de synchronisation.
 * Orchestrateur du processus de sync offline → online.
 */
@Service
@Transactional
public class SynchronizationServiceImpl implements SynchronizationService {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizationServiceImpl.class);

    private final SyncBatchRepository syncBatchRepository;
    private final ReconciliationService reconciliationService;

    @Autowired
    public SynchronizationServiceImpl(SyncBatchRepository syncBatchRepository,
                                      ReconciliationService reconciliationService) {
        this.syncBatchRepository = syncBatchRepository;
        this.reconciliationService = reconciliationService;
    }

    @Override
    public SyncResponseDto initiateSynchronization(SyncRequestDto request) {
        logger.info("Initiation de la synchronisation: wallet={}, device={}",
                request.getWalletId(), request.getDeviceId());

        // Vérifier qu'il n'y a pas déjà une sync en cours
        SyncBatch latestBatch = syncBatchRepository.findLatestByWalletId(request.getWalletId())
                .orElse(null);

        if (latestBatch != null &&
                (latestBatch.getStatus() == SyncBatchStatus.INITIATED ||
                        latestBatch.getStatus() == SyncBatchStatus.VALIDATING ||
                        latestBatch.getStatus() == SyncBatchStatus.PROCESSING)) {
            throw new BusinessException("Une synchronisation est déjà en cours pour ce wallet");
        }

        // Déléguer à la reconciliation
        return reconciliationService.synchronize(request);
    }

    @Override
    @Transactional(readOnly = true)
    public SyncResponseDto checkSynchronizationStatus(UUID batchId) {
        logger.info("Vérification du statut de sync: {}", batchId);

        SyncBatch batch = syncBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException("Lot de sync introuvable: " + batchId));

        SyncResponseDto response = new SyncResponseDto();
        response.setBatchId(batch.getBatchId());
        response.setWalletId(batch.getWalletId());
        response.setStatus(batch.getStatus());
        response.setTransactionCount(batch.getTransactionCount());
        response.setStartedAt(batch.getStartedAt());
        response.setCompletedAt(batch.getCompletedAt());
        response.setTotalAmountSynced(batch.getTotalAmount());
        response.setDiscrepancyReport(reconciliationService.generateDiscrepancyReport(batchId));

        return response;
    }

    @Override
    public SyncResponseDto retrySynchronization(UUID batchId) {
        logger.info("Nouvelle tentative de sync: {}", batchId);

        SyncBatch batch = syncBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException("Lot de sync introuvable: " + batchId));

        if (batch.getStatus() != SyncBatchStatus.FAILED && batch.getStatus() != SyncBatchStatus.PARTIAL) {
            throw new BusinessException("Seuls les lots FAILED ou PARTIAL peuvent être relancés");
        }

        // Réinitialiser le statut
        batch.setStatus(SyncBatchStatus.INITIATED);
        batch.setStartedAt(java.time.LocalDateTime.now());
        batch.setCompletedAt(null);
        syncBatchRepository.save(batch);

        // Relancer la reconciliation
        SyncRequestDto request = new SyncRequestDto();
        request.setWalletId(batch.getWalletId());
        request.setDeviceId(batch.getDeviceId());

        return reconciliationService.synchronize(request);
    }

    @Override
    public void abortSynchronization(UUID batchId) {
        logger.info("Abandon de la synchronisation: {}", batchId);

        SyncBatch batch = syncBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException("Lot de sync introuvable: " + batchId));

        if (batch.getStatus() == SyncBatchStatus.COMPLETED) {
            throw new BusinessException("Impossible d'annuler une synchronisation complétée");
        }

        batch.setStatus(SyncBatchStatus.FAILED);
        syncBatchRepository.save(batch);

        reconciliationService.cancelReconciliation(batchId);
    }
}