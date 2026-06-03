package com.paylogic.paywalletlite.controller.admin;

import com.paylogic.paywalletlite.dto.request.FundWalletRequestDto;
import com.paylogic.paywalletlite.dto.request.WalletConfigUpdateRequestDto;
import com.paylogic.paywalletlite.dto.response.WalletConfigResponseDto;
import com.paylogic.paywalletlite.dto.response.WalletResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.security.AuthenticationFacade;
import com.paylogic.paywalletlite.service.wallet.WalletConfigService;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur REST pour les opérations d'administration des wallets.
 *
 * Tous les endpoints de ce contrôleur nécessitent le rôle ADMIN.
 * La sécurité est renforcée avec @PreAuthorize en plus de la vérification
 * programmatique dans AuthenticationFacade.
 *
 * Route de base : /v1/admin/wallets
 */
@RestController
@RequestMapping("/v1/admin/wallets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWalletController {

    private final WalletService walletService;
    private final WalletConfigService walletConfigService;
    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public AdminWalletController(WalletService walletService,
                                 WalletConfigService walletConfigService,
                                 AuthenticationFacade authenticationFacade) {
        this.walletService = walletService;
        this.walletConfigService = walletConfigService;
        this.authenticationFacade = authenticationFacade;
    }

    // ============================================================
    // CONSULTATION (ADMIN)
    // ============================================================

    /**
     * GET /v1/admin/wallets/pending
     *
     * Liste tous les wallets en attente d'approbation.
     * Réservé aux administrateurs.
     *
     * @return Liste de WalletResponseDto avec statut PENDING_APPROVAL
     */
    @GetMapping("/pending")
    public ResponseEntity<List<WalletResponseDto>> getPendingWallets() {
        assertAdminAccess();
        List<WalletResponseDto> wallets = walletService.getPendingWallets();
        return ResponseEntity.ok(wallets);
    }

    /**
     * GET /v1/admin/wallets/user/{userId}
     *
     * Liste tous les wallets d'un utilisateur spécifique.
     * Réservé aux administrateurs.
     *
     * @param userId ID de l'utilisateur cible
     * @return Liste de WalletResponseDto
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletResponseDto>> getWalletsByUser(
            @PathVariable("userId") UUID userId) {

        assertAdminAccess();
        List<WalletResponseDto> wallets = walletService.getWalletsByUserId(userId);
        return ResponseEntity.ok(wallets);
    }

    /**
     * GET /v1/admin/wallets/{id}
     *
     * Retourne les détails d'un wallet spécifique (sans restriction de propriétaire).
     * Réservé aux administrateurs.
     *
     * @param walletId ID du wallet
     * @return WalletResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDto> getWalletById(@PathVariable("id") UUID walletId) {
        assertAdminAccess();
        WalletResponseDto wallet = walletService.getWalletById(walletId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * GET /v1/admin/wallets/{id}/config
     *
     * Retourne la configuration d'un wallet spécifique.
     * Réservé aux administrateurs.
     *
     * @param walletId ID du wallet
     * @return WalletConfigResponseDto
     */
    @GetMapping("/{id}/config")
    public ResponseEntity<WalletConfigResponseDto> getWalletConfig(@PathVariable("id") UUID walletId) {
        assertAdminAccess();
        WalletResponseDto wallet = walletService.getWalletById(walletId);

        if (wallet.getConfig() == null) {
            throw new BusinessException("No configuration found for wallet: " + walletId);
        }
        return ResponseEntity.ok(wallet.getConfig());
    }

    // ============================================================
    // WORKFLOW APPROBATION (ADMIN)
    // ============================================================

    /**
     * POST /v1/admin/wallets/{id}/approve
     *
     * Approuve un wallet en attente.
     * Status: PENDING_APPROVAL → APPROVED
     *
     * @param walletId ID du wallet à approuver
     * @param notes    Notes optionnelles d'approbation (query param)
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<WalletResponseDto> approveWallet(
            @PathVariable("id") UUID walletId,
            @RequestParam(value = "notes", defaultValue = "") String notes) {

        assertAdminAccess();
        WalletResponseDto wallet = walletService.approveWallet(walletId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/admin/wallets/{id}/reject
     *
     * Rejette un wallet en attente.
     * Status: PENDING_APPROVAL → REJECTED
     *
     * @param walletId ID du wallet à rejeter
     * @param reason   Raison du rejet (obligatoire)
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<WalletResponseDto> rejectWallet(
            @PathVariable("id") UUID walletId,
            @RequestParam("reason") String reason) {

        assertAdminAccess();

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Rejection reason is required");
        }

        WalletResponseDto wallet = walletService.rejectWallet(walletId, reason);
        return ResponseEntity.ok(wallet);
    }

    /**
     * PUT /v1/admin/wallets/{id}/configure
     *
     * Configure un wallet approuvé (limites, règles, etc.).
     * Status: APPROVED → CONFIGURED
     *
     * @param walletId  ID du wallet à configurer
     * @param configDto DTO contenant la configuration
     * @return WalletResponseDto mis à jour
     */
    @PutMapping("/{id}/configure")
    public ResponseEntity<WalletResponseDto> configureWallet(
            @PathVariable("id") UUID walletId,
            @Valid @RequestBody WalletConfigUpdateRequestDto configDto) {

        assertAdminAccess();
        WalletResponseDto wallet = walletService.configureWallet(walletId, configDto);
        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/admin/wallets/{id}/activate
     *
     * Active un wallet configuré.
     * Status: CONFIGURED → ACTIVE
     *
     * @param walletId ID du wallet à activer
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<WalletResponseDto> activateWallet(@PathVariable("id") UUID walletId) {
        assertAdminAccess();
        WalletResponseDto wallet = walletService.activateWallet(walletId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/admin/wallets/{id}/fund
     *
     * Finance un wallet spécifique.
     * Réservé aux administrateurs.
     *
     * @param walletId ID du wallet à financer
     * @param request  DTO contenant le montant et la source
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/fund")
    public ResponseEntity<WalletResponseDto> fundWallet(
            @PathVariable("id") UUID walletId,
            @Valid @RequestBody FundWalletRequestDto request) {

        assertAdminAccess();

        WalletResponseDto response = walletService.fundWallet(
                walletId,
                request.getAmount(),
                request.getCurrency(),
                request.getFundingSource(),
                request.getExternalReference(),
                request.getNotes()
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // GESTION DES STATUTS (ADMIN)
    // ============================================================

    /**
     * POST /v1/admin/wallets/{id}/lock
     *
     * Verrouille un wallet actif.
     * Status: ACTIVE → LOCKED
     *
     * @param walletId ID du wallet à verrouiller
     * @param reason   Raison du verrouillage (obligatoire)
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/lock")
    public ResponseEntity<WalletResponseDto> lockWallet(
            @PathVariable("id") UUID walletId,
            @RequestParam("reason") String reason) {

        assertAdminAccess();

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Lock reason is required");
        }

        WalletResponseDto wallet = walletService.lockWallet(walletId, reason);
        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/admin/wallets/{id}/unlock
     *
     * Déverrouille un wallet verrouillé.
     * Status: LOCKED → ACTIVE
     *
     * @param walletId ID du wallet à déverrouiller
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/unlock")
    public ResponseEntity<WalletResponseDto> unlockWallet(@PathVariable("id") UUID walletId) {
        assertAdminAccess();
        WalletResponseDto wallet = walletService.unlockWallet(walletId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/admin/wallets/{id}/freeze
     *
     * Gèle un wallet (investigation, litige, demande judiciaire).
     * Status: ACTIVE → FROZEN
     *
     * @param walletId ID du wallet à geler
     * @param reason   Raison du gel (obligatoire)
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/freeze")
    public ResponseEntity<WalletResponseDto> freezeWallet(
            @PathVariable("id") UUID walletId,
            @RequestParam("reason") String reason) {

        assertAdminAccess();

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Freeze reason is required");
        }

        WalletResponseDto wallet = walletService.freezeWallet(walletId, reason);
        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/admin/wallets/{id}/close
     *
     * Clôture définitivement un wallet.
     * Status: * → CLOSED
     *
     * @param walletId ID du wallet à clôturer
     * @param reason   Raison de la clôture (obligatoire)
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/{id}/close")
    public ResponseEntity<WalletResponseDto> closeWallet(
            @PathVariable("id") UUID walletId,
            @RequestParam("reason") String reason) {

        assertAdminAccess();

        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Close reason is required");
        }

        WalletResponseDto wallet = walletService.closeWallet(walletId, reason);
        return ResponseEntity.ok(wallet);
    }

    // ============================================================
    // MÉTHODES PRIVÉES
    // ============================================================

    /**
     * Vérifie que l'utilisateur courant a le rôle ADMIN.
     * Double vérification : @PreAuthorize sur la classe + vérification programmatique.
     */
    private void assertAdminAccess() {
        if (!authenticationFacade.isCurrentUserAdmin()) {
            throw new SecurityException("Access denied: administrative privileges required");
        }
    }
}