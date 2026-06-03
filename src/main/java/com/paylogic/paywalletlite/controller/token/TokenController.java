package com.paylogic.paywalletlite.controller.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.dto.request.TokenAllocationRequestDto;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.dto.response.TokenChainResponseDto;
import com.paylogic.paywalletlite.dto.response.TokenResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.token.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/tokens")
public class TokenController {

    private final TokenService tokenService;
    private final TokenAllocationService tokenAllocationService;
    private final TokenGenerationService tokenGenerationService;
    private final TokenLifecycleService tokenLifecycleService;
    private final TokenRedemptionService tokenRedemptionService;
    private final TokenTransferTraceService tokenTransferTraceService;
    private final TokenValidationService tokenValidationService;
    private final TokenSignatureService tokenSignatureService;

    @Autowired
    public TokenController(TokenService tokenService,
                           TokenAllocationService tokenAllocationService,
                           TokenGenerationService tokenGenerationService,
                           TokenLifecycleService tokenLifecycleService,
                           TokenRedemptionService tokenRedemptionService,
                           TokenTransferTraceService tokenTransferTraceService,
                           TokenValidationService tokenValidationService,
                           TokenSignatureService tokenSignatureService) {
        this.tokenService = tokenService;
        this.tokenAllocationService = tokenAllocationService;
        this.tokenGenerationService = tokenGenerationService;
        this.tokenLifecycleService = tokenLifecycleService;
        this.tokenRedemptionService = tokenRedemptionService;
        this.tokenTransferTraceService = tokenTransferTraceService;
        this.tokenValidationService = tokenValidationService;
        this.tokenSignatureService = tokenSignatureService;
    }

    // ============================================================
    // 1. CONFIG & ALLOCATION (Génération + Allocation initiale)
    // ============================================================

    /**
     * POST /api/v1/tokens/allocate
     * Alloue des tokens depuis le solde online vers offline.
     * Déclenche : génération → signature → allocation → enregistrement émission.
     */
    @PostMapping("/allocate")
    public ResponseEntity<?> allocateTokens(@RequestParam UUID walletId,
                                            @RequestBody TokenAllocationRequestDto request) {
        try {
            List<Token> tokens = tokenService.allocateTokens(walletId, request);
            List<TokenResponseDto> response = tokens.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ALLOCATION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/tokens/allocate-offline
     * Allocation rapide pour paiement offline (montant fixe, config par défaut).
     */
    @PostMapping("/allocate-offline")
    public ResponseEntity<?> allocateForOffline(@RequestParam UUID walletId,
                                                @RequestParam BigDecimal amount) {
        try {
            List<Token> tokens = tokenService.allocateTokensForOffline(walletId, amount);
            List<TokenResponseDto> response = tokens.stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ALLOCATION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/tokens/can-allocate
     * Vérifie si un wallet peut allouer un montant donné.
     */
    @GetMapping("/can-allocate")
    public ResponseEntity<?> canAllocate(@RequestParam UUID walletId,
                                         @RequestParam BigDecimal amount) {
        boolean canAllocate = tokenAllocationService.canAllocate(walletId, amount);
        return ResponseEntity.ok(new ApiErrorResponseDto(
                canAllocate ? "YES" : "NO",
                canAllocate ? "Sufficient balance" : "Insufficient balance",
                null
        ));
    }

    // ============================================================
    // 2. QUERY & VALIDATION
    // ============================================================

    /**
     * GET /api/v1/tokens/{tokenId}
     * Détails d'un token.
     */
    @GetMapping("/{tokenId}")
    public ResponseEntity<?> getToken(@PathVariable UUID tokenId) {
        try {
            Token token = tokenService.findById(tokenId);
            return ResponseEntity.ok(toResponseDto(token));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/tokens/by-nonce/{nonce}
     * Recherche par nonce (pour vérification offline).
     */
    @GetMapping("/by-nonce/{nonce}")
    public ResponseEntity<?> getTokenByNonce(@PathVariable String nonce) {
        try {
            Token token = tokenService.findByNonce(nonce);
            return ResponseEntity.ok(toResponseDto(token));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/tokens/wallet/{walletId}
     * Tous les tokens d'un wallet.
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TokenResponseDto>> getWalletTokens(@PathVariable UUID walletId) {
        List<Token> tokens = tokenService.findByWalletId(walletId);
        return ResponseEntity.ok(tokens.stream().map(this::toResponseDto).collect(Collectors.toList()));
    }

    /**
     * GET /api/v1/tokens/wallet/{walletId}/status/{status}
     * Tokens filtrés par status.
     */
    @GetMapping("/wallet/{walletId}/status/{status}")
    public ResponseEntity<List<TokenResponseDto>> getWalletTokensByStatus(@PathVariable UUID walletId,
                                                                          @PathVariable TokenStatus status) {
        List<Token> tokens = tokenService.findByWalletIdAndStatus(walletId, status);
        return ResponseEntity.ok(tokens.stream().map(this::toResponseDto).collect(Collectors.toList()));
    }

    /**
     * POST /api/v1/tokens/{tokenId}/validate-offline
     * Vérifie si un token est valide pour un paiement offline.
     */
    @PostMapping("/{tokenId}/validate-offline")
    public ResponseEntity<?> validateTokenOffline(@PathVariable UUID tokenId,
                                                  @RequestParam UUID walletId) {
        try {
            boolean valid = tokenService.validateTokenForOfflinePayment(tokenId, walletId);
            return ResponseEntity.ok(new ApiErrorResponseDto(
                    valid ? "VALID" : "INVALID",
                    valid ? "Token is valid for offline payment" : "Token is not valid",
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("VALIDATION_ERROR", e.getMessage(), null));
        }
    }

    // ============================================================
    // 3. TRANSFER (Traçabilité de la chaîne de possession)
    // ============================================================

    /**
     * POST /api/v1/tokens/transfer
     * Enregistre un transfert P2P et met à jour la chaîne de possession.
     * Côté server uniquement — le vrai transfert offline se fait device-to-device.
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> recordTransfer(@RequestParam UUID tokenId,
                                            @RequestParam UUID fromWalletId,
                                            @RequestParam UUID toWalletId,
                                            @RequestParam String payerSignature,
                                            @RequestParam LocalDateTime transferTimestamp) {
        try {
            // Étape 1 : Exécuter le transfert métier
            TokenTransferNode node = tokenService.transferToken(tokenId, fromWalletId, toWalletId, payerSignature, transferTimestamp);

            // Étape 2 : Enregistrer dans la chaîne de possession
            tokenTransferTraceService.recordTransfer(
                    tokenService.findById(tokenId),
                    null, // Wallet résolu dans le service
                    null, // Wallet résolu dans le service
                    payerSignature
            );

            return ResponseEntity.ok(node);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("TRANSFER_ERROR", e.getMessage(), null));
        }
    }


    // ============================================================
    // 4. REDEMPTION (Consommation finale)
    // ============================================================

    /**
     * POST /api/v1/tokens/{tokenId}/redeem
     * Redeem un token unique : convertit en solde online du payee.
     */
    @PostMapping("/{tokenId}/redeem")
    public ResponseEntity<?> redeemToken(@PathVariable UUID tokenId,
                                         @RequestParam UUID walletId,
                                         @RequestParam(required = false) String validationSignature) {
        try {
            Token redeemed = tokenRedemptionService.redeemToken(tokenId, walletId, validationSignature);

            // Enregistrer le nœud de redemption dans la chaîne
            tokenTransferTraceService.recordRedemption(redeemed, null, validationSignature);

            return ResponseEntity.ok(new ApiErrorResponseDto(
                    "REDEEMED",
                    "Token redeemed successfully. Value added to online balance.",
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("REDEMPTION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/tokens/redeem-batch
     * Redeem un batch de tokens.
     */
    @PostMapping("/redeem-batch")
    public ResponseEntity<?> redeemTokens(@RequestParam UUID walletId,
                                          @RequestBody List<UUID> tokenIds) {
        try {
            List<Token> redeemed = tokenRedemptionService.redeemTokens(tokenIds, walletId);
            return ResponseEntity.ok(new ApiErrorResponseDto(
                    "REDEEMED",
                    redeemed.size() + " tokens redeemed successfully",
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("REDEMPTION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/tokens/{tokenId}/request-redemption
     * Marque un token comme PENDING_REDEMPTION (étape intermédiaire).
     */
    @PostMapping("/{tokenId}/request-redemption")
    public ResponseEntity<?> requestRedemption(@PathVariable UUID tokenId) {
        try {
            tokenLifecycleService.markPendingRedemption(tokenId);
            return ResponseEntity.ok(new ApiErrorResponseDto(
                    "PENDING_REDEMPTION",
                    "Token marked for redemption. Awaiting validation.",
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/tokens/{tokenId}/redemption-eligibility
     * Vérifie si un token est éligible au redemption.
     */
    @GetMapping("/{tokenId}/redemption-eligibility")
    public ResponseEntity<?> checkRedemptionEligibility(@PathVariable UUID tokenId,
                                                        @RequestParam UUID walletId) {
        boolean eligible = tokenRedemptionService.isEligibleForRedemption(tokenId, walletId);
        return ResponseEntity.ok(new ApiErrorResponseDto(
                eligible ? "ELIGIBLE" : "NOT_ELIGIBLE",
                eligible ? "Token can be redeemed" : "Token cannot be redeemed",
                null
        ));
    }

    // ============================================================
    // 5. CHAINE DE POSSESSION (Traceability)
    // ============================================================

    /**
     * GET /api/v1/tokens/{tokenId}/chain
     * Reconstruit la chaîne complète de possession.
     * Backend → Alice → Bob → Charlie
     */
    @GetMapping("/{tokenId}/chain")
    public ResponseEntity<?> getTokenChain(@PathVariable UUID tokenId) {
        try {
            List<TokenTransferNode> chain = tokenTransferTraceService.reconstructChain(tokenId);
            Token token = tokenService.findById(tokenId);

            TokenChainResponseDto response = new TokenChainResponseDto();
            response.setTokenId(token.getTokenId());
            response.setTokenValue(token.getValue());
            response.setTokenStatus(token.getStatus().name());
            response.setTransferCount(token.getTransferCount());
            response.setMaxTransfers(token.getMaxTransfers());

            int step = 1;
            for (TokenTransferNode node : chain) {
                TokenChainResponseDto.ChainLinkDto link = new TokenChainResponseDto.ChainLinkDto();
                link.setStep(step++);
                link.setAction(detectAction(node, chain, step - 1));
                link.setFromWalletId(node.getPayerWalletId());
                link.setFromWalletOwner(node.getPayerWallet() != null ? node.getPayerWallet().getUser().getPhoneNumber() : "Unknown");
                link.setToWalletId(node.getPayeeWalletId());
                link.setToWalletOwner(node.getPayeeWallet() != null ? node.getPayeeWallet().getUser().getPhoneNumber() : "Unknown");
                link.setAmount(node.getTransferredAmount());
                link.setTimestamp(node.getTransferTimestamp());
                link.setSignature(node.getPayerSignature());
                link.setTransferHash(node.getTransferHash());
                response.getChain().add(link);
            }

            return ResponseEntity.ok(response);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/tokens/{tokenId}/chain/verify
     * Vérifie l'intégrité cryptographique de la chaîne.
     */
    @GetMapping("/{tokenId}/chain/verify")
    public ResponseEntity<?> verifyChainIntegrity(@PathVariable UUID tokenId) {
        try {
            boolean valid = tokenTransferTraceService.verifyChainIntegrity(tokenId);
            return ResponseEntity.ok(new ApiErrorResponseDto(
                    valid ? "INTEGRITY_VALID" : "INTEGRITY_BROKEN",
                    valid ? "Chain integrity verified successfully" : "Chain integrity check failed",
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/tokens/{tokenId}/current-holder
     * Retourne le détenteur actuel du token.
     */
    @GetMapping("/{tokenId}/current-holder")
    public ResponseEntity<?> getCurrentHolder(@PathVariable UUID tokenId) {
        try {
            Wallet holder = tokenTransferTraceService.getCurrentHolder(tokenId);
            return ResponseEntity.ok(new ApiErrorResponseDto(
                    "SUCCESS",
                    "Current holder: " + holder.getWalletId(),
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    // ============================================================
    // 6. LIFECYCLE MANAGEMENT (Admin / Système)
    // ============================================================

    /**
     * POST /api/v1/tokens/{tokenId}/expire
     * Force l'expiration d'un token (admin ou job).
     */
    @PostMapping("/{tokenId}/expire")
    public ResponseEntity<?> expireToken(@PathVariable UUID tokenId) {
        try {
            tokenLifecycleService.expire(tokenId);
            return ResponseEntity.ok(new ApiErrorResponseDto("EXPIRED", "Token expired", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/tokens/{tokenId}/revoke
     * Révoque un token (admin).
     */
    @PostMapping("/{tokenId}/revoke")
    public ResponseEntity<?> revokeToken(@PathVariable UUID tokenId,
                                         @RequestParam String reason) {
        try {
            tokenLifecycleService.revoke(tokenId, reason);
            return ResponseEntity.ok(new ApiErrorResponseDto("REVOKED", "Token revoked: " + reason, null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/tokens/cleanup-expired
     * Nettoie les tokens expirés (job manuel).
     */
    @PostMapping("/cleanup-expired")
    public ResponseEntity<?> cleanupExpired() {
        tokenService.cleanupExpiredTokens();
        return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", "Expired tokens cleaned up", null));
    }

    // ============================================================
    // 7. STATISTICS
    // ============================================================

    /**
     * GET /api/v1/tokens/wallet/{walletId}/count
     * Nombre de tokens actifs.
     */
    @GetMapping("/wallet/{walletId}/count")
    public ResponseEntity<Long> countActiveTokens(@PathVariable UUID walletId) {
        return ResponseEntity.ok(tokenService.countActiveTokensByWallet(walletId));
    }

    /**
     * GET /api/v1/tokens/wallet/{walletId}/total-value
     * Valeur totale des tokens.
     */
    @GetMapping("/wallet/{walletId}/total-value")
    public ResponseEntity<BigDecimal> getTotalValue(@PathVariable UUID walletId) {
        return ResponseEntity.ok(tokenService.getTotalValueByWallet(walletId));
    }

    /**
     * GET /api/v1/tokens/wallet/{walletId}/history
     * Historique complet des transferts impliquant ce wallet.
     * @GetMapping("/wallet/{walletId}/history")
     *     public ResponseEntity<List<TokenTransferNode>> getWalletTransferHistory(@PathVariable UUID walletId) {
     *         List<TokenTransferNode> history = tokenTransferTraceService.getWalletTransferHistory(walletId);
     *         return ResponseEntity.ok(history);
     *     }
     */


    // ============================================================
    // UTILITAIRES
    // ============================================================

    private TokenResponseDto toResponseDto(Token token) {
        TokenResponseDto dto = new TokenResponseDto();
        dto.setTokenId(token.getTokenId());
        dto.setValue(token.getValue());
        dto.setStatus(token.getStatus());
        dto.setNonce(token.getNonce());
        dto.setTokenHash(token.getTokenHash());
        dto.setTokenSignature(token.getTokenSignature().getSignatureValue());
        dto.setIssuedAt(token.getIssuedAt());
        dto.setExpiresAt(token.getExpiresAt());
        dto.setTransferCount(token.getTransferCount());
        dto.setMaxTransfers(token.getMaxTransfers());
        dto.setHolderWalletId(token.getCurrentHolderWalletId());
        dto.setIssuerId(token.getIssuerId());
        dto.setOriginalWalletId(token.getOriginalWalletId());
        dto.setIssuerWalletId(token.getIssuerWalletId());
        dto.setAllocationMode(token.getAllocationMode() != null ? token.getAllocationMode().name() : null);
        return dto;
    }

    private String detectAction(TokenTransferNode node, List<TokenTransferNode> chain, int step) {
        if (step == 1) {
            return "ISSUANCE";
        }
        if (step == chain.size()) {
            // Dernier nœud — vérifier si redemption
            Token token = node.getToken();
            if (token != null && token.getStatus() == TokenStatus.REDEEMED) {
                return "REDEMPTION";
            }
        }
        return "TRANSFER";
    }
}