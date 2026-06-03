package com.paylogic.paywalletlite.controller.admin;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.service.security.KeyRotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/key-rotation")
public class KeyRotationController {

    private final KeyRotationService keyRotationService;

    @Autowired
    public KeyRotationController(KeyRotationService keyRotationService) {
        this.keyRotationService = keyRotationService;
    }

    /**
     * POST /api/v1/admin/key-rotation/rotate
     * Effectue la rotation manuelle d'une clé.
     */
    @PostMapping("/rotate")
    public ResponseEntity<?> rotateKey(@RequestParam ServerKeyPurpose purpose) {
        try {
            ServerKey newKey = keyRotationService.rotateKey(purpose);
            return ResponseEntity.ok(newKey);
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiErrorResponseDto("ROTATION_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/key-rotation/pending
     * Liste les clés nécessitant une rotation.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ServerKey>> getPendingRotations() {
        return ResponseEntity.ok(keyRotationService.findKeysNeedingRotation());
    }

    /**
     * POST /api/v1/admin/key-rotation/check
     * Déclenche la vérification et rotation automatique.
     */
    @PostMapping("/check")
    public ResponseEntity<?> checkAndRotate() {
        keyRotationService.checkAndRotateExpiringKeys();
        return ResponseEntity.ok(new ApiErrorResponseDto("SUCCESS", "Rotation check completed", null));
    }
}