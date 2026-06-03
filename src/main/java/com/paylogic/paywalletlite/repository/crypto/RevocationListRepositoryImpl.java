package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.RevocationList;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class RevocationListRepositoryImpl implements RevocationListRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public RevocationList save(RevocationList entry) {
        if (entry.getEntryId() == null) {
            entityManager.persist(entry);
            return entry;
        }
        return entityManager.merge(entry);
    }

    @Override
    public Optional<RevocationList> findById(UUID entryId) {
        return Optional.ofNullable(entityManager.find(RevocationList.class, entryId));
    }

    @Override
    public Optional<RevocationList> findByCertificateId(UUID certificateId) {
        TypedQuery<RevocationList> query = entityManager.createQuery(
                "SELECT rl FROM RevocationList rl WHERE rl.certificate.certificateId = :cid", RevocationList.class);
        query.setParameter("cid", certificateId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<RevocationList> findAll() {
        return entityManager.createQuery("SELECT rl FROM RevocationList rl ORDER BY rl.revokedAt DESC", RevocationList.class)
                .getResultList();
    }

    @Override
    public List<RevocationList> findByReason(String reason) {
        return entityManager.createQuery(
                        "SELECT rl FROM RevocationList rl WHERE rl.reason = :reason", RevocationList.class)
                .setParameter("reason", reason)
                .getResultList();
    }

    @Override
    public boolean existsByCertificateId(UUID certificateId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(rl) FROM RevocationList rl WHERE rl.certificate.certificateId = :cid", Long.class)
                .setParameter("cid", certificateId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void delete(RevocationList entry) {
        entityManager.remove(entityManager.contains(entry) ? entry : entityManager.merge(entry));
    }
}