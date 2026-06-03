package com.paylogic.paywalletlite.repository.notification;

import com.paylogic.paywalletlite.domain.notification.AuditLog;
import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation JPA du repository AuditLog.
 *
 * L'annotation @Repository est ESSENTIELLE pour que Spring
 * détecte ce bean et l'injecte dans AuditServiceImpl.
 */
@Repository
@Transactional
public class AuditLogRepositoryImpl implements AuditLogRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public AuditLog save(AuditLog auditLog) {
        if (auditLog.getAuditId() == null) {
            em.persist(auditLog);
            return auditLog;
        }
        return em.merge(auditLog);
    }

    @Override
    public Optional<AuditLog> findById(UUID auditId) {
        return Optional.ofNullable(em.find(AuditLog.class, auditId));
    }

    @Override
    public List<AuditLog> findAll() {
        TypedQuery<AuditLog> query = em.createQuery(
                "SELECT a FROM AuditLog a ORDER BY a.timestamp DESC",
                AuditLog.class);
        return query.getResultList();
    }

    @Override
    public List<AuditLog> findByEventType(AuditEventType eventType) {
        TypedQuery<AuditLog> query = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.eventType = :eventType ORDER BY a.timestamp DESC",
                AuditLog.class);
        query.setParameter("eventType", eventType);
        return query.getResultList();
    }

    @Override
    public List<AuditLog> findByTargetId(UUID targetId) {
        TypedQuery<AuditLog> query = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.targetId = :targetId ORDER BY a.timestamp DESC",
                AuditLog.class);
        query.setParameter("targetId", targetId);
        return query.getResultList();
    }

    @Override
    public List<AuditLog> findByActorId(UUID actorId) {
        TypedQuery<AuditLog> query = em.createQuery(
                "SELECT a FROM AuditLog a WHERE a.actorId = :actorId ORDER BY a.timestamp DESC",
                AuditLog.class);
        query.setParameter("actorId", actorId);
        return query.getResultList();
    }

    @Override
    public void delete(AuditLog auditLog) {
        em.remove(em.contains(auditLog) ? auditLog : em.merge(auditLog));
    }

    @Override
    public Optional<AuditLog> findLatest() {
        TypedQuery<AuditLog> query = em.createQuery(
                "SELECT a FROM AuditLog a ORDER BY a.timestamp DESC, a.auditId DESC",
                AuditLog.class);
        query.setMaxResults(1);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<AuditLog> findRecentEvents(int limit) {
        TypedQuery<AuditLog> query = em.createQuery(
                "SELECT a FROM AuditLog a ORDER BY a.timestamp DESC",
                AuditLog.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}

