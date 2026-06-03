package com.paylogic.paywalletlite.repository.identity;

import com.paylogic.paywalletlite.domain.identity.DeviceSession;
import com.paylogic.paywalletlite.domain.identity.enums.SessionStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class DeviceSessionRepositoryImpl implements DeviceSessionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public DeviceSession save(DeviceSession session) {
        if (session.getSessionId() == null) {
            entityManager.persist(session);
            return session;
        }
        return entityManager.merge(session);
    }

    @Override
    public Optional<DeviceSession> findById(UUID sessionId) {
        return Optional.ofNullable(entityManager.find(DeviceSession.class, sessionId));
    }

    @Override
    public Optional<DeviceSession> findByTokenHash(String jwtTokenHash) {
        TypedQuery<DeviceSession> query = entityManager.createQuery(
                "SELECT s FROM DeviceSession s WHERE s.jwtTokenHash = :hash", DeviceSession.class);
        query.setParameter("hash", jwtTokenHash);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<DeviceSession> findByDeviceId(UUID deviceId) {
        return entityManager.createQuery(
                        "SELECT s FROM DeviceSession s WHERE s.device.deviceId = :did", DeviceSession.class)
                .setParameter("did", deviceId)
                .getResultList();
    }

    @Override
    public List<DeviceSession> findByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT s FROM DeviceSession s WHERE s.user.userId = :uid", DeviceSession.class)
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    public List<DeviceSession> findActiveByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT s FROM DeviceSession s WHERE s.user.userId = :uid AND s.status = :status AND s.expiresAt > :now",
                        DeviceSession.class)
                .setParameter("uid", userId)
                .setParameter("status", SessionStatus.ACTIVE)
                .setParameter("now", LocalDateTime.now())
                .getResultList();
    }

    @Override
    public List<DeviceSession> findExpiredSessions() {
        return entityManager.createQuery(
                        "SELECT s FROM DeviceSession s WHERE s.expiresAt < :now AND s.status = :status",
                        DeviceSession.class)
                .setParameter("now", LocalDateTime.now())
                .setParameter("status", SessionStatus.ACTIVE)
                .getResultList();
    }

    @Override
    @Transactional
    public void delete(DeviceSession session) {
        entityManager.remove(entityManager.contains(session) ? session : entityManager.merge(session));
    }

    @Override
    @Transactional
    public void revokeSession(UUID sessionId) {
        entityManager.createQuery(
                        "UPDATE DeviceSession s SET s.status = :status WHERE s.sessionId = :id")
                .setParameter("status", SessionStatus.REVOKED)
                .setParameter("id", sessionId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void revokeAllUserSessions(UUID userId) {
        entityManager.createQuery(
                        "UPDATE DeviceSession s SET s.status = :status WHERE s.user.userId = :uid")
                .setParameter("status", SessionStatus.REVOKED)
                .setParameter("uid", userId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void revokeAllDeviceSessions(UUID deviceId) {
        entityManager.createQuery(
                        "UPDATE DeviceSession s SET s.status = :status WHERE s.device.deviceId = :did")
                .setParameter("status", SessionStatus.REVOKED)
                .setParameter("did", deviceId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateLastActivity(UUID sessionId) {
        entityManager.createQuery(
                        "UPDATE DeviceSession s SET s.lastActivity = :now WHERE s.sessionId = :id")
                .setParameter("now", LocalDateTime.now())
                .setParameter("id", sessionId)
                .executeUpdate();
    }

    @Override
    public long countActiveSessionsByUser(UUID userId) {
        return entityManager.createQuery(
                        "SELECT COUNT(s) FROM DeviceSession s WHERE s.user.userId = :uid AND s.status = :status AND s.expiresAt > :now",
                        Long.class)
                .setParameter("uid", userId)
                .setParameter("status", SessionStatus.ACTIVE)
                .setParameter("now", LocalDateTime.now())
                .getSingleResult();
    }
}