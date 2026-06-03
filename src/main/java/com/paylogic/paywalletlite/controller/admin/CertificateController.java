package com.paylogic.paywalletlite.controller.admin;

import com.paylogic.paywalletlite.domain.crypto.Certificate;
import com.paylogic.paywalletlite.domain.crypto.CertificateAuthority;
import com.paylogic.paywalletlite.dto.response.ApiErrorResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.mapper.CertificateMapper;
import com.paylogic.paywalletlite.service.security.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/admin/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificateMapper certificateMapper;

    @Autowired
    public CertificateController(CertificateService certificateService, CertificateMapper certificateMapper) {
        this.certificateService = certificateService;
        this.certificateMapper = certificateMapper;
    }

    /**
     * POST /api/v1/admin/certificates/ca/initialize
     * Initialise la CA racine (premier démarrage uniquement).
     */
    @PostMapping("/ca/initialize")
    public ResponseEntity<?> initializeRootCA(@RequestParam String caName) {
        try {
            CertificateAuthority ca = certificateService.initializeRootCA(caName);
            return ResponseEntity.status(HttpStatus.CREATED).body(ca);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("CA_INIT_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/certificates/ca/active
     * Récupère la CA active.
     */
    @GetMapping("/ca/active")
    public ResponseEntity<?> getActiveCA() {
        try {
            CertificateAuthority ca = certificateService.getActiveCA();
            return ResponseEntity.ok(ca);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/admin/certificates/issue
     * Émet un nouveau certificat pour un wallet.
     */
    @PostMapping("/issue")
    public ResponseEntity<?> issueCertificate(@RequestParam UUID walletId,
                                              @RequestParam UUID caId) {
        try {
            Certificate cert = certificateService.issueCertificate(walletId, caId);
            return ResponseEntity.ok(certificateMapper.toDto(cert));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ISSUE_ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/certificates/{certId}
     * Détails d'un certificat.
     */
    @GetMapping("/{certId}")
    public ResponseEntity<?> getCertificate(@PathVariable UUID certId) {
        try {
            Certificate cert = certificateService.findById(certId);
            return ResponseEntity.ok(cert);
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiErrorResponseDto("NOT_FOUND", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/admin/certificates/{certId}/validate
     * Valide un certificat.
     */
    @PostMapping("/{certId}/validate")
    public ResponseEntity<?> validateCertificate(@PathVariable UUID certId) {
        try {
            boolean valid = certificateService.validateCertificate(certId);
            return ResponseEntity.ok(new ApiErrorResponseDto(
                    valid ? "VALID" : "INVALID",
                    valid ? "Certificate is valid" : "Certificate is invalid or revoked",
                    null
            ));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * POST /api/v1/admin/certificates/{certId}/revoke
     * Révoque un certificat.
     */
    @PostMapping("/{certId}/revoke")
    public ResponseEntity<?> revokeCertificate(@PathVariable UUID certId,
                                               @RequestParam String reason) {
        try {
            certificateService.revokeCertificate(certId, reason);
            return ResponseEntity.ok(new ApiErrorResponseDto("REVOKED", "Certificate revoked: " + reason, null));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiErrorResponseDto("ERROR", e.getMessage(), null));
        }
    }

    /**
     * GET /api/v1/admin/certificates/wallet/{walletId}
     * Liste les certificats d'un wallet.
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Certificate>> getWalletCertificates(@PathVariable UUID walletId) {
        return ResponseEntity.ok(certificateService.findByWalletId(walletId));
    }
}