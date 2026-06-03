package com.paylogic.paywalletlite.controller.wallet;

import com.paylogic.paywalletlite.dto.request.CreateWalletRequestDto;
import com.paylogic.paywalletlite.dto.request.FundWalletRequestDto;
import com.paylogic.paywalletlite.dto.response.WalletConfigResponseDto;
import com.paylogic.paywalletlite.dto.response.WalletResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.security.AuthenticationFacade;
import com.paylogic.paywalletlite.service.wallet.WalletConfigService;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Contrôleur REST pour les opérations utilisateur standard sur les wallets.
 *
 * Tous les endpoints utilisent l'identité de l'utilisateur connecté
 * via le JWT. Aucun ID d'utilisateur n'est passé en paramètre.
 *
 * Route de base : /v1/wallets
 */
@RestController
@RequestMapping("/v1/wallets")
public class WalletController {

    private final WalletService walletService;
    private final WalletConfigService walletConfigService;
    private final AuthenticationFacade authenticationFacade;

    @Autowired
    public WalletController(WalletService walletService,
                            WalletConfigService walletConfigService,
                            AuthenticationFacade authenticationFacade) {
        this.walletService = walletService;
        this.walletConfigService = walletConfigService;
        this.authenticationFacade = authenticationFacade;
    }

    // ============================================================
    // CRÉATION DE WALLET
    // ============================================================

    /**
     * POST /v1/wallets/request
     *
     * Demande de création d'un wallet pour l'utilisateur connecté.
     * Le wallet créé aura le statut PENDING_APPROVAL et devra être
     * approuvé par un administrateur avant activation.
     *
     * @param request DTO contenant les informations de la demande
     * @return WalletResponseDto avec le wallet créé (statut PENDING_APPROVAL)
     */
    @PostMapping("/request")
    public ResponseEntity<WalletResponseDto> requestWalletCreation(
            @Valid @RequestBody CreateWalletRequestDto request) {

        UUID userId = resolveUserId(request);
        WalletResponseDto response = walletService.requestWalletCreation(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // CONSULTATION DE SES PROPRES WALLETS
    // ============================================================

    /**
     * GET /v1/wallets/me/all
     *
     * Retourne la liste de tous les wallets appartenant
     * à l'utilisateur connecté.
     *
     * @return Liste de WalletResponseDto
     */
    @GetMapping("/me/all")
    public ResponseEntity<List<WalletResponseDto>> getMyWallets() {
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        List<WalletResponseDto> wallets = walletService.getWalletsByUserId(currentUserId);
        return ResponseEntity.ok(wallets);
    }

    /**
     * GET /v1/wallets/me/active
     *
     * Retourne le wallet ACTIVE de l'utilisateur connecté.
     * Lance une exception si aucun wallet actif n'est trouvé.
     *
     * @return WalletResponseDto du wallet actif
     */
    @GetMapping("/me/active")
    public ResponseEntity<WalletResponseDto> getMyActiveWallet() {
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        WalletResponseDto wallet = walletService.getActiveWalletByUserId(currentUserId);
        return ResponseEntity.ok(wallet);
    }

    /**
     * GET /v1/wallets/me/config
     *
     * Retourne la configuration du wallet actif de l'utilisateur connecté.
     *
     * @return WalletConfigResponseDto
     */
    @GetMapping("/me/config")
    public ResponseEntity<WalletConfigResponseDto> getMyWalletConfig() {
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        WalletResponseDto wallet = walletService.getActiveWalletByUserId(currentUserId);

        if (wallet.getConfig() == null) {
            throw new BusinessException("No configuration found for your active wallet");
        }
        return ResponseEntity.ok(wallet.getConfig());
    }

    // ============================================================
    // CONSULTATION D'UN WALLET SPÉCIFIQUE (PROPRIÉTAIRE UNIQUEMENT)
    // ============================================================

    /**
     * GET /v1/wallets/{id}
     *
     * Retourne les détails d'un wallet spécifique.
     * L'utilisateur doit être le propriétaire du wallet.
     *
     * @param walletId ID du wallet à consulter
     * @return WalletResponseDto
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDto> getWalletById(@PathVariable("id") UUID walletId) {
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        WalletResponseDto wallet = walletService.getWalletById(walletId);

        if (!wallet.getUserId().equals(currentUserId)) {
            throw new SecurityException("Access denied: this wallet does not belong to you");
        }

        return ResponseEntity.ok(wallet);
    }

    /**
     * POST /v1/wallets/me/fund
     *
     * Finance le wallet de l'utilisateur connecté.
     * L'utilisateur ne peut financer que son propre wallet actif.
     *
     * @param request DTO contenant le montant et la source
     * @return WalletResponseDto mis à jour
     */
    @PostMapping("/me/fund")
    public ResponseEntity<WalletResponseDto> fundMyWallet(
            @Valid @RequestBody FundWalletRequestDto request) {

        UUID currentUserId = authenticationFacade.getCurrentUserId();
        WalletResponseDto activeWallet = walletService.getActiveWalletByUserId(currentUserId);

        WalletResponseDto response = walletService.fundWallet(
                activeWallet.getWalletId(),
                request.getAmount(),
                request.getCurrency(),
                request.getFundingSource(),
                request.getExternalReference(),
                request.getNotes()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /v1/wallets/{id}/config
     *
     * Retourne la configuration d'un wallet spécifique.
     * L'utilisateur doit être le propriétaire du wallet.
     *
     * @param walletId ID du wallet
     * @return WalletConfigResponseDto
     */
    @GetMapping("/{id}/config")
    public ResponseEntity<WalletConfigResponseDto> getWalletConfigById(@PathVariable("id") UUID walletId) {
        UUID currentUserId = authenticationFacade.getCurrentUserId();
        WalletResponseDto wallet = walletService.getWalletById(walletId);

        if (!wallet.getUserId().equals(currentUserId)) {
            throw new SecurityException("Access denied: this wallet does not belong to you");
        }

        if (wallet.getConfig() == null) {
            throw new BusinessException("No configuration found for wallet: " + walletId);
        }

        return ResponseEntity.ok(wallet.getConfig());
    }

    // ============================================================
    // MÉTHODES PRIVÉES
    // ============================================================

    /**
     * Résout l'ID utilisateur : JWT d'abord, fallback body.
     */
    private UUID resolveUserId(CreateWalletRequestDto request) {
        try {
            return authenticationFacade.getCurrentUserId();
        } catch (SecurityException e) {
            if (request.getUserId() != null) {
                return request.getUserId();
            }
            throw new SecurityException("No authenticated user found and no userId in request body");
        }
    }
}