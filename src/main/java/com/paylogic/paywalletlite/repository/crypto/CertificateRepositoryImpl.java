package com.paylogic.paywalletlite.repository.crypto;

import com.paylogic.paywalletlite.domain.crypto.Certificate;
import com.paylogic.paywalletlite.domain.crypto.enums.CertificateStatus;
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
public class CertificateRepositoryImpl implements CertificateRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Certificate save(Certificate certificate) {
        if (certificate.getCertificateId() == null) {
            entityManager.persist(certificate);
            return certificate;
        }
        return entityManager.merge(certificate);
    }

    @Override
    public Optional<Certificate> findById(UUID certificateId) {
        return Optional.ofNullable(entityManager.find(Certificate.class, certificateId));
    }

    @Override
    public Optional<Certificate> findByThumbprint(String thumbprint) {
        TypedQuery<Certificate> query = entityManager.createQuery(
                "SELECT c FROM Certificate c WHERE c.thumbprint = :thumb", Certificate.class);
        query.setParameter("thumb", thumbprint);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Certificate> findByWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT c FROM Certificate c WHERE c.wallet.walletId = :wid", Certificate.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public List<Certificate> findByStatus(CertificateStatus status) {
        return entityManager.createQuery(
                        "SELECT c FROM Certificate c WHERE c.status = :status", Certificate.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Certificate> findByIssuerCaId(UUID caId) {
        return entityManager.createQuery(
                        "SELECT c FROM Certificate c WHERE c.issuerCa.caId = :caid", Certificate.class)
                .setParameter("caid", caId)
                .getResultList();
    }

    @Override
    public List<Certificate> findExpiringCertificates(LocalDateTime threshold) {
        return entityManager.createQuery(
                        "SELECT c FROM Certificate c WHERE c.expiresAt < :threshold AND c.status = :status", Certificate.class)
                .setParameter("threshold", threshold)
                .setParameter("status", CertificateStatus.VALID)
                .getResultList();
    }

    @Override
    public boolean existsByThumbprint(String thumbprint) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Certificate c WHERE c.thumbprint = :thumb", Long.class)
                .setParameter("thumb", thumbprint)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void updateStatus(UUID certificateId, CertificateStatus status) {
        entityManager.createQuery(
                        "UPDATE Certificate c SET c.status = :status WHERE c.certificateId = :id")
                .setParameter("status", status)
                .setParameter("id", certificateId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void delete(Certificate certificate) {
        entityManager.remove(entityManager.contains(certificate) ? certificate : entityManager.merge(certificate));
    }
}