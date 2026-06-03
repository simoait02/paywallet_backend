package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;
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
public class ServerKeyRepositoryImpl implements ServerKeyRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public ServerKey save(ServerKey serverKey) {
        if (serverKey.getServerKeyId() == null) {
            entityManager.persist(serverKey);
            return serverKey;
        }
        return entityManager.merge(serverKey);
    }

    @Override
    public Optional<ServerKey> findById(UUID serverKeyId) {
        return Optional.ofNullable(entityManager.find(ServerKey.class, serverKeyId));
    }

    @Override
    public Optional<ServerKey> findActiveByPurpose(ServerKeyPurpose purpose) {
        TypedQuery<ServerKey> query = entityManager.createQuery(
                "SELECT sk FROM ServerKey sk WHERE sk.keyPurpose = :purpose AND sk.status = :status AND (sk.expiresAt IS NULL OR sk.expiresAt > :now)",
                ServerKey.class);
        query.setParameter("purpose", purpose);
        query.setParameter("status", ServerKeyStatus.ACTIVE);
        query.setParameter("now", LocalDateTime.now());
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ServerKey> findByStatus(ServerKeyStatus status) {
        return entityManager.createQuery(
                        "SELECT sk FROM ServerKey sk WHERE sk.status = :status", ServerKey.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<ServerKey> findByPurpose(ServerKeyPurpose purpose) {
        return entityManager.createQuery(
                        "SELECT sk FROM ServerKey sk WHERE sk.keyPurpose = :purpose ORDER BY sk.createdAt DESC", ServerKey.class)
                .setParameter("purpose", purpose)
                .getResultList();
    }

    @Override
    public List<ServerKey> findExpiringKeys(LocalDateTime threshold) {
        return entityManager.createQuery(
                        "SELECT sk FROM ServerKey sk WHERE sk.expiresAt < :threshold AND sk.status = :status", ServerKey.class)
                .setParameter("threshold", threshold)
                .setParameter("status", ServerKeyStatus.ACTIVE)
                .getResultList();
    }

    @Override
    public Optional<ServerKey> findByWalletIdAndPurpose(UUID walletId, ServerKeyPurpose purpose) {
        TypedQuery<ServerKey> query = entityManager.createQuery(
                "SELECT sk FROM ServerKey sk WHERE sk.wallet.walletId = :wid AND sk.keyPurpose = :purpose AND sk.status = :status", ServerKey.class);
        query.setParameter("wid", walletId);
        query.setParameter("purpose", purpose);
        query.setParameter("status", ServerKeyStatus.ACTIVE);
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByPurposeAndStatus(ServerKeyPurpose purpose, ServerKeyStatus status) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(sk) FROM ServerKey sk WHERE sk.keyPurpose = :purpose AND sk.status = :status", Long.class)
                .setParameter("purpose", purpose)
                .setParameter("status", status)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void updateStatus(UUID serverKeyId, ServerKeyStatus status) {
        entityManager.createQuery(
                        "UPDATE ServerKey sk SET sk.status = :status WHERE sk.serverKeyId = :id")
                .setParameter("status", status)
                .setParameter("id", serverKeyId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateRotatedAt(UUID serverKeyId, LocalDateTime rotatedAt) {
        entityManager.createQuery(
                        "UPDATE ServerKey sk SET sk.rotatedAt = :rotated WHERE sk.serverKeyId = :id")
                .setParameter("rotated", rotatedAt)
                .setParameter("id", serverKeyId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void delete(ServerKey serverKey) {
        entityManager.remove(entityManager.contains(serverKey) ? serverKey : entityManager.merge(serverKey));
    }
}