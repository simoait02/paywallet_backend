package com.paylogic.paywalletlite.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * One entry in the provisioning bundle's list of trusted server keys. The device
 * uses these public keys to verify {@code TokenSignature} blobs offline.
 *
 * Multiple keys are returned to handle rotation: a token signed under an older
 * (but still trusted) key remains verifiable on devices that have refreshed
 * their provisioning bundle since the rotation.
 */
public class TrustedServerKeyDto {

    private UUID keyId;
    private String publicKeyPem;
    private String purpose;
    private String status;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public TrustedServerKeyDto() {}

    public TrustedServerKeyDto(UUID keyId, String publicKeyPem, String purpose,
                               String status, LocalDateTime validFrom, LocalDateTime validTo) {
        this.keyId = keyId;
        this.publicKeyPem = publicKeyPem;
        this.purpose = purpose;
        this.status = status;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public UUID getKeyId() { return keyId; }
    public void setKeyId(UUID keyId) { this.keyId = keyId; }

    public String getPublicKeyPem() { return publicKeyPem; }
    public void setPublicKeyPem(String publicKeyPem) { this.publicKeyPem = publicKeyPem; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
}
