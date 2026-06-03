package com.paylogic.paywalletlite.controller.token;

import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.token.TokenDenomination;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import com.paylogic.paywalletlite.dto.request.TokenAllocationConfigRequestDto;
import com.paylogic.paywalletlite.dto.request.TokenDenominationRequestDto;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.dto.response.TokenAllocationConfigResponseDto;
import com.paylogic.paywalletlite.dto.response.TokenDenominationResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.token.TokenAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/token-configs")
public class TokenAllocationController {

    private final TokenAllocationService configService;

    @Autowired
    public TokenAllocationController(TokenAllocationService configService) {
        this.configService = configService;
    }

    // ============================================================
    // CONFIGURATION ROUTES
    // ============================================================

    /**
     * POST /api/v1/token-configs
     * Crée une nouvelle configuration d'allocation.
     */
    @PostMapping
    public ResponseEntity<?> createConfig(@Valid @RequestBody TokenAllocationConfigRequestDto request,
                                          @RequestParam(defaultValue = "SYSTEM") String createdBy) {
        try {
            TokenAllocationConfig config = configService.createConfig(request, createdBy);
            return ResponseEntity.status(HttpStatus.CREATED).body(toConfigResponse(config));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("CONFIG_ERROR", e.getMessage(), null));
        }
    }

    /**
     * PUT /api/v1/token-configs/{configId}
     * Met à jour une configuration existante.
     */
    @PutMapping("/{configId}")
    public ResponseEntity<?> updateConfig(@PathVariable UUID configId,
                                          @Valid @RequestBody TokenAllocationConfigRequestDto request) {
        try {
            TokenAllocationConfig config = configService.updateConfig(configId, request);
            return ResponseEntity.ok(toConfigResponse(config));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("CONFIG_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/token-configs/{configId}
     * Récupère une configuration par ID.
     */
    @GetMapping("/{configId}")
    public ResponseEntity<?> getConfig(@PathVariable UUID configId) {
        try {
            TokenAllocationConfig config = configService.findConfigById(configId);
            return ResponseEntity.ok(toConfigResponse(config));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/token-configs
     * Liste toutes les configurations actives.
     */
    @GetMapping
    public ResponseEntity<List<TokenAllocationConfigResponseDto>> getAllActiveConfigs() {
        List<TokenAllocationConfig> configs = configService.findAllActiveConfigs();
        List<TokenAllocationConfigResponseDto> response = new ArrayList<TokenAllocationConfigResponseDto>();

        for (TokenAllocationConfig config : configs) {
            response.add(toConfigResponse(config));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/token-configs/wallet-type/{walletType}
     * Liste les configurations par type de wallet.
     */
    @GetMapping("/wallet-type/{walletType}")
    public ResponseEntity<List<TokenAllocationConfigResponseDto>> getConfigsByWalletType(@PathVariable WalletType walletType) {
        List<TokenAllocationConfig> configs = configService.findConfigsByWalletType(walletType);
        List<TokenAllocationConfigResponseDto> response = new ArrayList<TokenAllocationConfigResponseDto>();

        for (TokenAllocationConfig config : configs) {
            response.add(toConfigResponse(config));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/token-configs/{configId}/activate
     * Active une configuration.
     */
    @PostMapping("/{configId}/activate")
    public ResponseEntity<?> activateConfig(@PathVariable UUID configId) {
        try {
            configService.activateConfig(configId);
            return ResponseEntity.ok(new ApiErrorResponseDto("ACTIVATED", "Config activated", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/token-configs/{configId}/deprecate
     * Déprécie une configuration.
     */
    @PostMapping("/{configId}/deprecate")
    public ResponseEntity<?> deprecateConfig(@PathVariable UUID configId) {
        try {
            configService.deprecateConfig(configId);
            return ResponseEntity.ok(new ApiErrorResponseDto("DEPRECATED", "Config deprecated", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/token-configs/{configId}/denominations
     * Récupère les dénominations triées d'une configuration.
     */
    @GetMapping("/{configId}/denominations")
    public ResponseEntity<?> getConfigDenominations(@PathVariable UUID configId) {
        try {
            List<BigDecimal> values = configService.getSortedDenominationValues(configId);
            return ResponseEntity.ok(values);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    // ============================================================
    // DENOMINATION ROUTES (directement dans ce controller)
    // ============================================================

    /**
     * POST /api/v1/token-configs/denominations
     * Crée une nouvelle dénomination.
     */
    @PostMapping("/denominations")
    public ResponseEntity<?> createDenomination(@Valid @RequestBody TokenDenominationRequestDto request) {
        try {
            TokenDenomination denomination = configService.createDenomination(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(toDenominationResponse(denomination));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("DENOMINATION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * PUT /api/v1/token-configs/denominations/{denominationId}
     * Met à jour une dénomination.
     */
    @PutMapping("/denominations/{denominationId}")
    public ResponseEntity<?> updateDenomination(@PathVariable UUID denominationId,
                                                @Valid @RequestBody TokenDenominationRequestDto request) {
        try {
            TokenDenomination denomination = configService.updateDenomination(denominationId, request);
            return ResponseEntity.ok(toDenominationResponse(denomination));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("DENOMINATION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/token-configs/denominations
     * Liste toutes les dénominations actives.
     */
    @GetMapping("/denominations")
    public ResponseEntity<List<TokenDenominationResponseDto>> getAllActiveDenominations() {
        List<TokenDenomination> denominations = configService.findAllActiveDenominations();
        List<TokenDenominationResponseDto> response = new ArrayList<TokenDenominationResponseDto>();

        for (TokenDenomination denom : denominations) {
            response.add(toDenominationResponse(denom));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/token-configs/denominations/currency/{currencyCode}
     * Liste les dénominations par code devise.
     */
    @GetMapping("/denominations/currency/{currencyCode}")
    public ResponseEntity<List<TokenDenominationResponseDto>> getDenominationsByCurrency(@PathVariable String currencyCode) {
        List<TokenDenomination> denominations = configService.findDenominationsByCurrencyCode(currencyCode);
        List<TokenDenominationResponseDto> response = new ArrayList<TokenDenominationResponseDto>();

        for (TokenDenomination denom : denominations) {
            response.add(toDenominationResponse(denom));
        }

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/token-configs/denominations/{denominationId}/activate
     * Active une dénomination.
     */
    @PostMapping("/denominations/{denominationId}/activate")
    public ResponseEntity<?> activateDenomination(@PathVariable UUID denominationId) {
        try {
            configService.activateDenomination(denominationId);
            return ResponseEntity.ok(new ApiErrorResponseDto("ACTIVATED", "Denomination activated", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/token-configs/denominations/{denominationId}/deactivate
     * Désactive une dénomination.
     */
    @PostMapping("/denominations/{denominationId}/deactivate")
    public ResponseEntity<?> deactivateDenomination(@PathVariable UUID denominationId) {
        try {
            configService.deactivateDenomination(denominationId);
            return ResponseEntity.ok(new ApiErrorResponseDto("DEACTIVATED", "Denomination deactivated", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private TokenAllocationConfigResponseDto toConfigResponse(TokenAllocationConfig config) {
        TokenAllocationConfigResponseDto dto = new TokenAllocationConfigResponseDto();
        dto.setConfigId(config.getConfigId());
        dto.setConfigName(config.getConfigName());
        dto.setWalletType(config.getWalletType());
        dto.setDensityThreshold(config.getDensityThreshold());
        dto.setSlidingWindowSize(config.getSlidingWindowSize());
        dto.setMaxTokenCount(config.getMaxTokenCount());
        dto.setMinSingleTokenValue(config.getMinSingleTokenValue());
        dto.setMaxSingleTokenValue(config.getMaxSingleTokenValue());
        dto.setMaxTransfersPerToken(config.getMaxTransfersPerToken());
        dto.setTokenLifetimeHours(config.getTokenLifetimeHours());
        dto.setAllowOverpayment(config.getAllowOverpayment());
        dto.setMaxOverpaymentThreshold(config.getMaxOverpaymentThreshold());
        dto.setStatus(config.getStatus());
        dto.setCreatedAt(config.getCreatedAt());
        dto.setCreatedBy(config.getCreatedBy());

        if (config.getDenominations() != null) {
            for (TokenDenomination denom : config.getDenominations()) {
                dto.getDenominations().add(toDenominationResponse(denom));
            }
        }

        return dto;
    }

    private TokenDenominationResponseDto toDenominationResponse(TokenDenomination denomination) {
        TokenDenominationResponseDto dto = new TokenDenominationResponseDto();
        dto.setDenominationId(denomination.getDenominationId());
        dto.setValue(denomination.getValue());
        dto.setCurrencyCode(denomination.getCurrencyCode());
        dto.setIsActive(denomination.getIsActive());
        dto.setPriorityOrder(denomination.getPriorityOrder());
        dto.setDensityWeight(denomination.getDensityWeight());
        dto.setMinAllocationAmount(denomination.getMinAllocationAmount());
        dto.setMaxAllocationAmount(denomination.getMaxAllocationAmount());
        dto.setCreatedAt(denomination.getCreatedAt());
        return dto;
    }
}