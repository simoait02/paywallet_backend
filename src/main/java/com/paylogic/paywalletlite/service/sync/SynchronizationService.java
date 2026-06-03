package com.paylogic.paywalletlite.service.sync;

import com.paylogic.paywalletlite.dto.request.SyncRequestDto;
import com.paylogic.paywalletlite.dto.response.SyncResponseDto;

import java.util.UUID;

/**
 * Service de synchronisation coordonnant le processus complet.
 * Fait le lien entre le mobile (offline) et le backend (online).
 */
public interface SynchronizationService {

    /**
     * Lance une synchronisation complète pour un device.
     */
    SyncResponseDto initiateSynchronization(SyncRequestDto request);

    /**
     * Vérifie l'état d'une synchronisation en cours.
     */
    SyncResponseDto checkSynchronizationStatus(UUID batchId);

    /**
     * Force une nouvelle tentative de sync pour un lot échoué.
     */
    SyncResponseDto retrySynchronization(UUID batchId);

    /**
     * Annule une synchronisation en cours.
     */
    void abortSynchronization(UUID batchId);
}