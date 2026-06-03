package com.paylogic.paywalletlite.controller.sync;

import com.paylogic.paywalletlite.dto.request.SyncRequestDto;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.dto.response.SyncResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.security.AuthenticationFacade;
import com.paylogic.paywalletlite.service.sync.SynchronizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Contrôleur REST pour la synchronisation des transactions offline.
 *
 * Gère le cycle de vie complet :
 * 1. Soumission d'un batch de transactions offline
 * 2. Vérification du statut d'un batch
 * 3. Relance d'un batch échoué
 * 4. Annulation d'un batch en cours
 *
 * Route de base : /v1/sync
 */
@RestController
@RequestMapping("/v1/sync")
public class SynchronizationController {

    private static final Logger logger = LoggerFactory.getLogger(SynchronizationController.class);

    private final SynchronizationService synchronizationService;
    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public SynchronizationController(SynchronizationService synchronizationService,
                                     AuthenticationFacade authenticationFacade) {
        this.synchronizationService = synchronizationService;
        this.authenticationFacade = authenticationFacade;
    }

    // ============================================================
    // SYNCHRONISATION PRINCIPALE
    // ============================================================

    /**
     * POST /v1/sync
     *
     * Soumet un batch de transactions offline pour synchronisation.
     *
     * Le device du receiver envoie l'ensemble des transactions reçues
     * en mode offline. Chaque transaction contient les tokens avec leur
     * chaîne de transfert complète.
     *
     * Processus :
     * 1. Création du SyncBatch
     * 2. Persistance des transactions + OfflineTransactionToken
     * 3. Validation (signatures, chaînes, anti-double-spend)
     * 4. Redemption des tokens
     * 5. Mise à jour des soldes
     * 6. Enregistrement dans le ledger
     * 7. Gestion du surpaiement
     *
     * @param request DTO contenant le batch de transactions offline
     * @return SyncResponseDto avec le résultat complet
     */
    @PostMapping
    public ResponseEntity<SyncResponseDto> synchronize(
            @Valid @RequestBody SyncRequestDto request) {

        // Vérifier que le walletId correspond à l'utilisateur connecté
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        validateWalletOwnership(request.getWalletId(), currentUserId);

        logger.info("=== SYNCHRONISATION DEMANDÉE ===");
        logger.info("Wallet: {}, Device: {}, Transactions: {}",
                request.getWalletId(), request.getDeviceId(),
                request.getTransactions() != null ? request.getTransactions().size() : 0);

        try {
            SyncResponseDto response = synchronizationService.initiateSynchronization(request);
            logger.info("=== SYNCHRONISATION TERMINÉE : {} ===", response.getStatus());
            return ResponseEntity.ok(response);

        } catch (BusinessException e) {
            logger.error("Échec synchronisation: {}", e.getMessage());
            throw e;
        }
    }

    // ============================================================
    // VÉRIFICATION DU STATUT
    // ============================================================

    /**
     * GET /v1/sync/{batchId}/status
     *
     * Vérifie le statut d'un batch de synchronisation.
     *
     * @param batchId ID du batch à vérifier
     * @return SyncResponseDto avec le statut actuel
     */
    @GetMapping("/{batchId}/status")
    public ResponseEntity<SyncResponseDto> checkStatus(@PathVariable UUID batchId) {

        logger.info("Vérification statut batch: {}", batchId);

        SyncResponseDto response = synchronizationService.checkSynchronizationStatus(batchId);

        // Vérifier que le wallet appartient à l'utilisateur connecté
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        validateWalletOwnership(response.getWalletId(), currentUserId);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // RELANCE D'UNE SYNCHRONISATION ÉCHOUÉE
    // ============================================================

    /**
     * POST /v1/sync/{batchId}/retry
     *
     * Relance un batch de synchronisation qui a échoué.
     * Seuls les batches en statut FAILED ou PARTIAL peuvent être relancés.
     *
     * @param batchId ID du batch à relancer
     * @return SyncResponseDto avec le nouveau résultat
     */
    @PostMapping("/{batchId}/retry")
    public ResponseEntity<SyncResponseDto> retrySynchronization(@PathVariable UUID batchId) {

        logger.info("Relance synchronisation batch: {}", batchId);

        try {
            SyncResponseDto response = synchronizationService.retrySynchronization(batchId);
            logger.info("Relance terminée: {}", response.getStatus());
            return ResponseEntity.ok(response);

        } catch (BusinessException e) {
            logger.error("Échec relance: {}", e.getMessage());
            throw e;
        }
    }

    // ============================================================
    // ANNULATION D'UNE SYNCHRONISATION
    // ============================================================

    /**
     * POST /v1/sync/{batchId}/abort
     *
     * Annule un batch de synchronisation en cours.
     * Les transactions sont libérées et repassent en OFFLINE_PENDING.
     * Les crédits effectués sont annulés.
     *
     * @param batchId ID du batch à annuler
     * @return Confirmation
     */
    @PostMapping("/{batchId}/abort")
    public ResponseEntity<ApiErrorResponseDto> abortSynchronization(@PathVariable UUID batchId) {

        logger.info("Abandon synchronisation batch: {}", batchId);

        try {
            synchronizationService.abortSynchronization(batchId);
            return ResponseEntity.ok(
                    new ApiErrorResponseDto("SUCCESS", "Synchronization aborted", null));

        } catch (BusinessException e) {
            logger.error("Échec abandon: {}", e.getMessage());
            throw e;
        }
    }

    // ============================================================
    // HISTORIQUE DES SYNCHRONISATIONS
    // ============================================================

    /**
     * GET /v1/sync/history
     *
     * Récupère l'historique des synchronisations pour le wallet connecté.
     * Retourne les 20 derniers batches.
     *
     * @return Liste des SyncResponseDto récents
     */
    @GetMapping("/history")
    public ResponseEntity<?> getSyncHistory() {

        UUID currentUserId = authenticationFacade.getCurrentUserId();
        logger.info("Historique sync pour user: {}", currentUserId);

        // TODO: Implémenter la recherche par userId dans SynchronizationService
        // Pour l'instant, retourne une réponse indicative
        return ResponseEntity.ok(
                new ApiErrorResponseDto("INFO", "Sync history endpoint — à implémenter", null));
    }

    // ============================================================
    // RAPPORT DE DISCREPANCY
    // ============================================================

    /**
     * GET /v1/sync/{batchId}/report
     *
     * Génère un rapport détaillé de discrepancy pour un batch.
     *
     * @param batchId ID du batch
     * @return Rapport formaté en texte
     */
    @GetMapping("/{batchId}/report")
    public ResponseEntity<String> getDiscrepancyReport(@PathVariable UUID batchId) {

        logger.info("Rapport discrepancy batch: {}", batchId);

        try {
            String report = synchronizationService.checkSynchronizationStatus(batchId)
                    .getDiscrepancyReport();
            return ResponseEntity.ok(report);

        } catch (BusinessException e) {
            logger.error("Échec génération rapport: {}", e.getMessage());
            throw e;
        }
    }

    // ============================================================
    // MÉTHODES PRIVÉES
    // ============================================================

    /**
     * Vérifie que le wallet appartient bien à l'utilisateur connecté.
     *
     * @param walletId ID du wallet
     * @param currentUserId ID de l'utilisateur connecté
     */
    private void validateWalletOwnership(UUID walletId, UUID currentUserId) {
        // TODO: Implémenter la vérification via WalletService
        // Pour l'instant, on log un avertissement
        if (walletId == null) {
            throw new SecurityException("Wallet ID is required");
        }
        logger.debug("Validation propriété wallet: {} pour user: {}", walletId, currentUserId);
    }

    // ============================================================
    // GESTION DES EXCEPTIONS
    // ============================================================

    /**
     * Handler pour les exceptions de type BusinessException.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponseDto> handleBusinessException(BusinessException e) {
        logger.error("Business error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiErrorResponseDto("SYNC_ERROR", e.getMessage(), null));
    }

    /**
     * Handler pour les exceptions de type SecurityException.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponseDto> handleSecurityException(SecurityException e) {
        logger.error("Security error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponseDto("ACCESS_DENIED", e.getMessage(), null));
    }

    /**
     * Handler pour les exceptions génériques.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGenericException(Exception e) {
        logger.error("Unexpected error during synchronization", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponseDto("INTERNAL_ERROR",
                        "An unexpected error occurred during synchronization", null));
    }
}