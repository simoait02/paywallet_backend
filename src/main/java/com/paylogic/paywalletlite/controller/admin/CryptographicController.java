package com.paylogic.paywalletlite.controller.admin;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.security.CryptographicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/crypto")
public class CryptographicController {

    private final CryptographicService cryptographicService;

    @Autowired
    public CryptographicController(CryptographicService cryptographicService) {
        this.cryptographicService = cryptographicService;
    }

    /**
     * POST /api/v1/admin/crypto/keys/generate
     * Génère une nouvelle paire de clés pour un usage donné.
     */
    @PostMapping("/keys/generate")
    public ResponseEntity<?> generateKey(@RequestParam ServerKeyPurpose purpose,
                                         @RequestParam(required = false) UUID walletId) {
        try {
            ServerKey key = cryptographicService.generateKeyPair(purpose, walletId);
            return ResponseEntity.status(HttpStatus.CREATED).body(key);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("KEY_GENERATION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/crypto/keys/active
     * Récupère la clé active pour un usage.
     */
    @GetMapping("/keys/active")
    public ResponseEntity<?> getActiveKey(@RequestParam ServerKeyPurpose purpose) {
        try {
            ServerKey key = cryptographicService.getActiveKey(purpose);
            return ResponseEntity.ok(key);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/crypto/keys/{keyId}
     * Détails d'une clé.
     */
    @GetMapping("/keys/{keyId}")
    public ResponseEntity<?> getKey(@PathVariable UUID keyId) {
        try {
            ServerKey key = cryptographicService.findById(keyId);
            return ResponseEntity.ok(key);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/crypto/keys/{keyId}/public
     * Récupère la clé publique (pour distribution aux clients).
     */
    @GetMapping("/keys/{keyId}/public")
    public ResponseEntity<?> getPublicKey(@PathVariable UUID keyId) {
        try {
            String publicKey = cryptographicService.getPublicKey(keyId);
            return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", publicKey, null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/crypto/keys/purpose/{purpose}
     * Liste les clés par usage.
     */
    @GetMapping("/keys/purpose/{purpose}")
    public ResponseEntity<List<ServerKey>> getKeysByPurpose(@PathVariable ServerKeyPurpose purpose) {
        return ResponseEntity.ok(cryptographicService.findByPurpose(purpose));
    }

    /**
     * GET /api/v1/admin/crypto/keys/status/{status}
     * Liste les clés par status.
     */
    @GetMapping("/keys/status/{status}")
    public ResponseEntity<List<ServerKey>> getKeysByStatus(@PathVariable ServerKeyStatus status) {
        return ResponseEntity.ok(cryptographicService.findByStatus(status));
    }

    /**
     * POST /api/v1/admin/crypto/keys/{keyId}/revoke
     * Révoque une clé.
     */
    @PostMapping("/keys/{keyId}/revoke")
    public ResponseEntity<?> revokeKey(@PathVariable UUID keyId,
                                       @RequestParam String reason) {
        try {
            cryptographicService.revokeKey(keyId, reason);
            return ResponseEntity.ok(new ApiErrorResponseDto("REVOKED", "Key revoked: " + reason, null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * DELETE /api/v1/admin/crypto/keys/{keyId}
     * Supprime une clé (uniquement si expirée/révoquée).
     */
    @DeleteMapping("/keys/{keyId}")
    public ResponseEntity<?> deleteKey(@PathVariable UUID keyId) {
        try {
            cryptographicService.deleteKey(keyId);
            return ResponseEntity.ok(new ApiErrorResponseDto("DELETED", "Key deleted", null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }
}