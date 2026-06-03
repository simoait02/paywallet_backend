package com.paylogic.paywalletlite.domain.crypto;

import com.paylogic.paywalletlite.domain.crypto.enums.CertificateStatus;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "revocation_lists", schema = "pwl_app")
public class RevocationList {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "entry_id", updatable = false, nullable = false)
    private UUID entryId;

    // Relation vers le certificat révoqué
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_id", nullable = false)
    private Certificate certificate;

    @Column(name = "certificate_id", insertable = false, updatable = false)
    private UUID certificateId;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "revoked_by", length = 100)
    private String revokedBy;

    @Column(name = "crl_entry_serial", length = 255)
    private String crlEntrySerial;

    public RevocationList() {
        this.revokedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public UUID getEntryId() { return entryId; }
    public void setEntryId(UUID entryId) { this.entryId = entryId; }

    public Certificate getCertificate() { return certificate; }
    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
        if (certificate != null) {
            this.certificateId = certificate.getCertificateId();
        }
    }

    public UUID getCertificateId() {
        return certificateId != null ? certificateId :
                (certificate != null ? certificate.getCertificateId() : null);
    }

    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRevokedBy() { return revokedBy; }
    public void setRevokedBy(String revokedBy) { this.revokedBy = revokedBy; }

    public String getCrlEntrySerial() { return crlEntrySerial; }
    public void setCrlEntrySerial(String crlEntrySerial) { this.crlEntrySerial = crlEntrySerial; }
}