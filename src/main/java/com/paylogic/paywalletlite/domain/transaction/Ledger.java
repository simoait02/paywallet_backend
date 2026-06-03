package com.paylogic.paywalletlite.domain.transaction;

import com.paylogic.paywalletlite.domain.transaction.enums.LedgerType;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledgers", schema = "pwl_app")
public class Ledger {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "ledger_id", updatable = false, nullable = false)
    private UUID ledgerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ledger_version", length = 20)
    private String ledgerVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private LedgerType type;

    @Column(name = "ledger_hash", length = 255)
    private String ledgerHash;

    @Column(name = "is_sealed")
    private Boolean isSealed;

    public Ledger() {
        this.createdAt = LocalDateTime.now();
        this.isSealed = false;
    }

    // Getters et Setters
    public UUID getLedgerId() { return ledgerId; }
    public void setLedgerId(UUID ledgerId) { this.ledgerId = ledgerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLedgerVersion() { return ledgerVersion; }
    public void setLedgerVersion(String ledgerVersion) { this.ledgerVersion = ledgerVersion; }

    public LedgerType getType() { return type; }
    public void setType(LedgerType type) { this.type = type; }

    public String getLedgerHash() { return ledgerHash; }
    public void setLedgerHash(String ledgerHash) { this.ledgerHash = ledgerHash; }

    public Boolean getIsSealed() { return isSealed; }
    public void setIsSealed(Boolean isSealed) { this.isSealed = isSealed; }
}