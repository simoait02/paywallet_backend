package com.paylogic.paywalletlite.domain.risk;

import com.paylogic.paywalletlite.domain.risk.enums.RiskLevel;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "risk_profiles", schema = "pwl_app")
public class RiskProfile {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "profile_id", updatable = false, nullable = false)
    private UUID profileId;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel level;

    @Column(name = "assessed_at", nullable = false)
    private LocalDateTime assessedAt;

    @Column(name = "assessed_by", length = 100)
    private String assessedBy;

    @Column(name = "assessment_method", length = 100)
    private String assessmentMethod;

    @Column(name = "transaction_velocity")
    private Integer transactionVelocity;

    @Column(name = "average_transaction_amount", precision = 19, scale = 2)
    private BigDecimal averageTransactionAmount;

    @Column(name = "dispute_count")
    private Integer disputeCount;

    public RiskProfile() {
        this.assessedAt = LocalDateTime.now();
        this.level = RiskLevel.LOW;
        this.riskScore = 0;
        this.disputeCount = 0;
    }

    // Getters et Setters
    public UUID getProfileId() { return profileId; }
    public void setProfileId(UUID profileId) { this.profileId = profileId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public RiskLevel getLevel() { return level; }
    public void setLevel(RiskLevel level) { this.level = level; }

    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }

    public String getAssessedBy() { return assessedBy; }
    public void setAssessedBy(String assessedBy) { this.assessedBy = assessedBy; }

    public String getAssessmentMethod() { return assessmentMethod; }
    public void setAssessmentMethod(String assessmentMethod) { this.assessmentMethod = assessmentMethod; }

    public Integer getTransactionVelocity() { return transactionVelocity; }
    public void setTransactionVelocity(Integer transactionVelocity) { this.transactionVelocity = transactionVelocity; }

    public BigDecimal getAverageTransactionAmount() { return averageTransactionAmount; }
    public void setAverageTransactionAmount(BigDecimal averageTransactionAmount) { this.averageTransactionAmount = averageTransactionAmount; }

    public Integer getDisputeCount() { return disputeCount; }
    public void setDisputeCount(Integer disputeCount) { this.disputeCount = disputeCount; }
}