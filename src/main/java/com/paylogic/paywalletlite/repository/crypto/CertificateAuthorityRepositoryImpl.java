package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.CertificateAuthority;
import com.paylogic.paywalletlite.domain.crypto.enums.CAStatus;
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
public class CertificateAuthorityRepositoryImpl implements CertificateAuthorityRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public CertificateAuthority save(CertificateAuthority ca) {
        if (ca.getCaId() == null) {
            entityManager.persist(ca);
            return ca;
        }
        return entityManager.merge(ca);
    }

    @Override
    public Optional<CertificateAuthority> findById(UUID caId) {
        return Optional.ofNullable(entityManager.find(CertificateAuthority.class, caId));
    }

    @Override
    public Optional<CertificateAuthority> findActive() {
        TypedQuery<CertificateAuthority> query = entityManager.createQuery(
                "SELECT ca FROM CertificateAuthority ca WHERE ca.status = :status", CertificateAuthority.class);
        query.setParameter("status", CAStatus.ACTIVE);
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CertificateAuthority> findByStatus(CAStatus status) {
        return entityManager.createQuery(
                        "SELECT ca FROM CertificateAuthority ca WHERE ca.status = :status", CertificateAuthority.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public boolean existsByCaName(String caName) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(ca) FROM CertificateAuthority ca WHERE ca.caName = :name", Long.class)
                .setParameter("name", caName)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void updateStatus(UUID caId, CAStatus status) {
        entityManager.createQuery(
                        "UPDATE CertificateAuthority ca SET ca.status = :status WHERE ca.caId = :id")
                .setParameter("status", status)
                .setParameter("id", caId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void delete(CertificateAuthority ca) {
        entityManager.remove(entityManager.contains(ca) ? ca : entityManager.merge(ca));
    }
}