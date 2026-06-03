package com.paylogic.paywalletlite.domain.identity;

import com.paylogic.paywalletlite.domain.identity.enums.KYCStatus;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "kyc_profiles", schema = "pwl_app")
public class KYCProfile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "kyc_id", updatable = false, nullable = false)
    private UUID kycId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private KYCStatus status;

    @Column(name = "document_type", length = 50)
    private String documentType;

    @Column(name = "card_number", length = 100)
    private String cardNumber;

    // =====================================================
    // DOCUMENTS KYC
    // =====================================================

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "selfie_picture")
    private byte[] selfiePicture;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "cin_recto")
    private byte[] cinRecto;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "cin_verso")
    private byte[] cinVerso;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public KYCProfile() {
        this.status = KYCStatus.PENDING;
    }

    // Getters et Setters
    public UUID getKycId() { return kycId; }
    public void setKycId(UUID kycId) { this.kycId = kycId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public KYCStatus getStatus() { return status; }
    public void setStatus(KYCStatus status) { this.status = status; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}