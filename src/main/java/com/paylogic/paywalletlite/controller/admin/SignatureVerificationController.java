package com.paylogic.paywalletlite.controller.admin;

import com.paylogic.paywalletlite.dto.request.SignatureVerifyRequestDto;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.service.security.SignatureVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verify")
public class SignatureVerificationController {

    private final SignatureVerificationService signatureVerificationService;

    @Autowired
    public SignatureVerificationController(SignatureVerificationService signatureVerificationService) {
        this.signatureVerificationService = signatureVerificationService;
    }

    /**
     * POST /api/v1/verify/signature
     * Vérifie une signature ECDSA générique (accessible publiquement).
     */
    @PostMapping("/signature")
    public ResponseEntity<?> verifySignature(@RequestBody SignatureVerifyRequestDto request) {
        boolean valid = signatureVerificationService.verifyWithPublicKey(
                request.getData(),
                request.getSignature(),
                request.getPublicKey()
        );
        return ResponseEntity.ok(new ApiErrorResponseDto(
                valid ? "VALID" : "INVALID",
                valid ? "Signature verified successfully" : "Signature verification failed",
                null
        ));
    }

    /**
     * POST /api/v1/verify/offline-transfer
     * Vérifie une signature de transfert offline.
     */
    @PostMapping("/offline-transfer")
    public ResponseEntity<?> verifyOfflineTransfer(@RequestBody SignatureVerifyRequestDto request) {
        boolean valid = signatureVerificationService.verifyOfflineTransfer(
                request.getData(),
                request.getSignature(),
                request.getPublicKey()
        );
        return ResponseEntity.ok(new ApiErrorResponseDto(
                valid ? "VALID" : "INVALID",
                valid ? "Offline transfer signature valid" : "Offline transfer signature invalid",
                null
        ));
    }
}