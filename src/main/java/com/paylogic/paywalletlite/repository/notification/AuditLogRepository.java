package com.paylogic.paywalletlite.repository.notification;

import com.paylogic.paywalletlite.domain.notification.AuditLog;
import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    Optional<AuditLog> findById(UUID auditId);
    List<AuditLog> findAll();
    Optional<AuditLog> findLatest();
    List<AuditLog> findByEventType(AuditEventType eventType);
    List<AuditLog> findByTargetId(UUID targetId);
    List<AuditLog> findByActorId(UUID actorId);
    void delete(AuditLog auditLog);
    List<AuditLog> findRecentEvents(int limit);
}