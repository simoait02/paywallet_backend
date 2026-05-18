package com.paylogic.paywalletlite.domain.notification;

import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs", schema = "pwl_app")
public class AuditLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "audit_id", updatable = false, nullable = false)
    private UUID auditId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private AuditEventType eventType;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_type", length = 50)
    private String actorType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "action", nullable = false, length = 255)
    private String action;

    @Column(name = "details_json", length = 4000)
    private String detailsJson;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "integrity_hash", length = 255)
    private String integrityHash;

    @Column(name = "previous_audit_hash", length = 255)
    private String previousAuditHash;

    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    // Getters et Setters
    public UUID getAuditId() { return auditId; }
    public void setAuditId(UUID auditId) { this.auditId = auditId; }

    public AuditEventType getEventType() { return eventType; }
    public void setEventType(AuditEventType eventType) { this.eventType = eventType; }

    public UUID getActorId() { return actorId; }
    public void setActorId(UUID actorId) { this.actorId = actorId; }

    public String getActorType() { return actorType; }
    public void setActorType(String actorType) { this.actorType = actorType; }

    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getIntegrityHash() { return integrityHash; }
    public void setIntegrityHash(String integrityHash) { this.integrityHash = integrityHash; }

    public String getPreviousAuditHash() { return previousAuditHash; }
    public void setPreviousAuditHash(String previousAuditHash) { this.previousAuditHash = previousAuditHash; }
}