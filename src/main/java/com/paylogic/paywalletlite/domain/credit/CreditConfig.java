package com.paylogic.paywalletlite.domain.credit;

import com.paylogic.paywalletlite.domain.credit.enums.CreditConfigStatus;
import com.paylogic.paywalletlite.domain.risk.enums.RiskLevel;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "credit_configs", schema = "pwl_app")
public class CreditConfig {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "config_id", updatable = false, nullable = false)
    private UUID configId;

    @Column(name = "config_name", nullable = false, length = 100)
    private String configName;

    @Enumerated(EnumType.STRING)
    @Column(name = "min_risk_level", nullable = false, length = 20)
    private RiskLevel minRiskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "max_risk_level", nullable = false, length = 20)
    private RiskLevel maxRiskLevel;

    @Column(name = "max_credit_limit", precision = 19, scale = 2)
    private BigDecimal maxCreditLimit;

    @Column(name = "min_credit_limit", precision = 19, scale = 2)
    private BigDecimal minCreditLimit;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "repayment_period_days")
    private Integer repaymentPeriodDays;

    @Column(name = "auto_approval")
    private Boolean autoApproval;

    @Column(name = "requires_human_validation")
    private Boolean requiresHumanValidation;

    @Column(name = "max_offline_transactions")
    private Integer maxOfflineTransactions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CreditConfigStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    public CreditConfig() {
        this.createdAt = LocalDateTime.now();
        this.status = CreditConfigStatus.ACTIVE;
    }

    // Getters et Setters
    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId; }

    public String getConfigName() { return configName; }
    public void setConfigName(String configName) { this.configName = configName; }

    public RiskLevel getMinRiskLevel() { return minRiskLevel; }
    public void setMinRiskLevel(RiskLevel minRiskLevel) { this.minRiskLevel = minRiskLevel; }

    public RiskLevel getMaxRiskLevel() { return maxRiskLevel; }
    public void setMaxRiskLevel(RiskLevel maxRiskLevel) { this.maxRiskLevel = maxRiskLevel; }

    public BigDecimal getMaxCreditLimit() { return maxCreditLimit; }
    public void setMaxCreditLimit(BigDecimal maxCreditLimit) { this.maxCreditLimit = maxCreditLimit; }

    public BigDecimal getMinCreditLimit() { return minCreditLimit; }
    public void setMinCreditLimit(BigDecimal minCreditLimit) { this.minCreditLimit = minCreditLimit; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public Integer getRepaymentPeriodDays() { return repaymentPeriodDays; }
    public void setRepaymentPeriodDays(Integer repaymentPeriodDays) { this.repaymentPeriodDays = repaymentPeriodDays; }

    public Boolean getAutoApproval() { return autoApproval; }
    public void setAutoApproval(Boolean autoApproval) { this.autoApproval = autoApproval; }

    public Boolean getRequiresHumanValidation() { return requiresHumanValidation; }
    public void setRequiresHumanValidation(Boolean requiresHumanValidation) { this.requiresHumanValidation = requiresHumanValidation; }

    public Integer getMaxOfflineTransactions() { return maxOfflineTransactions; }
    public void setMaxOfflineTransactions(Integer maxOfflineTransactions) { this.maxOfflineTransactions = maxOfflineTransactions; }

    public CreditConfigStatus getStatus() { return status; }
    public void setStatus(CreditConfigStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}