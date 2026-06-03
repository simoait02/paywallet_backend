package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.enums.OfflineTokenValidationStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation manuelle du repository OfflineTransactionToken.
 *
 * Utilise JPA EntityManager pour les opérations de persistance.
 */
@Repository
@Transactional(readOnly = true)
public class OfflineTransactionTokenRepositoryImpl implements OfflineTransactionTokenRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public OfflineTransactionToken save(OfflineTransactionToken token) {
        if (token.getId() == null) {
            em.persist(token);
            return token;
        }
        return em.merge(token);
    }

    @Override
    @Transactional
    public Optional<OfflineTransactionToken> findById(UUID id) {
        return Optional.ofNullable(em.find(OfflineTransactionToken.class, id));
    }

    @Override
    public List<OfflineTransactionToken> findAll() {
        TypedQuery<OfflineTransactionToken> query = em.createQuery(
                "SELECT o FROM OfflineTransactionToken o ORDER BY o.createdAt DESC",
                OfflineTransactionToken.class);
        return query.getResultList();
    }

    @Override
    public void delete(OfflineTransactionToken token) {
        em.remove(em.contains(token) ? token : em.merge(token));
    }

    @Override
    public void deleteById(UUID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public boolean existsById(UUID id) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(o) FROM OfflineTransactionToken o WHERE o.id = :id",
                Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(o) FROM OfflineTransactionToken o", Long.class);
        return query.getSingleResult();
    }

    // ============================================================
    // MÉTHODES SPÉCIFIQUES
    // ============================================================

    @Override
    public List<OfflineTransactionToken> findByTransactionId(UUID transactionId) {
        TypedQuery<OfflineTransactionToken> query = em.createQuery(
                "SELECT o FROM OfflineTransactionToken o WHERE o.transactionId = :txId " +
                        "ORDER BY o.sequenceNumber ASC",
                OfflineTransactionToken.class);
        query.setParameter("txId", transactionId);
        return query.getResultList();
    }

    @Override
    public Optional<OfflineTransactionToken> findByTransactionIdAndTokenId(
            UUID transactionId, UUID tokenId) {
        TypedQuery<OfflineTransactionToken> query = em.createQuery(
                "SELECT o FROM OfflineTransactionToken o " +
                        "WHERE o.transactionId = :txId AND o.tokenId = :tokenId",
                OfflineTransactionToken.class);
        query.setParameter("txId", transactionId);
        query.setParameter("tokenId", tokenId);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<OfflineTransactionToken> findByTransactionIdAndValidationStatus(
            UUID transactionId, OfflineTokenValidationStatus status) {
        TypedQuery<OfflineTransactionToken> query = em.createQuery(
                "SELECT o FROM OfflineTransactionToken o " +
                        "WHERE o.transactionId = :txId AND o.validationStatus = :status " +
                        "ORDER BY o.sequenceNumber ASC",
                OfflineTransactionToken.class);
        query.setParameter("txId", transactionId);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public boolean existsByTokenIdAndValidationStatus(
            UUID tokenId, OfflineTokenValidationStatus status) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(o) FROM OfflineTransactionToken o " +
                        "WHERE o.tokenId = :tokenId AND o.validationStatus = :status",
                Long.class);
        query.setParameter("tokenId", tokenId);
        query.setParameter("status", status);
        return query.getSingleResult() > 0;
    }

    @Override
    public long countByTransactionIdAndValidationStatus(
            UUID transactionId, OfflineTokenValidationStatus status) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(o) FROM OfflineTransactionToken o " +
                        "WHERE o.transactionId = :txId AND o.validationStatus = :status",
                Long.class);
        query.setParameter("txId", transactionId);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    // ============================================================
    // MÉTHODES SUPPLÉMENTAIRES UTILES
    // ============================================================

    /**
     * Récupère tous les tokens d'un batch (via les transactions du batch).
     */
    public List<OfflineTransactionToken> findBySyncBatchId(UUID batchId) {
        TypedQuery<OfflineTransactionToken> query = em.createQuery(
                "SELECT o FROM OfflineTransactionToken o " +
                        "WHERE o.transactionId IN (" +
                        "   SELECT t.transactionId FROM Transaction t WHERE t.syncBatchId = :batchId" +
                        ") ORDER BY o.createdAt ASC",
                OfflineTransactionToken.class);
        query.setParameter("batchId", batchId);
        return query.getResultList();
    }

    /**
     * Compte les tokens par statut pour un batch.
     */
    public long countBySyncBatchIdAndValidationStatus(UUID batchId,
                                                      OfflineTokenValidationStatus status) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(o) FROM OfflineTransactionToken o " +
                        "WHERE o.transactionId IN (" +
                        "   SELECT t.transactionId FROM Transaction t WHERE t.syncBatchId = :batchId" +
                        ") AND o.validationStatus = :status",
                Long.class);
        query.setParameter("batchId", batchId);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    /**
     * Supprime les tokens validés d'un batch (nettoyage).
     */
    public int deleteRedeemedByBatchId(UUID batchId) {
        return em.createQuery(
                        "DELETE FROM OfflineTransactionToken o " +
                                "WHERE o.transactionId IN (" +
                                "   SELECT t.transactionId FROM Transaction t WHERE t.syncBatchId = :batchId" +
                                ") AND o.validationStatus = :status")
                .setParameter("batchId", batchId)
                .setParameter("status", OfflineTokenValidationStatus.REDEEMED)
                .executeUpdate();
    }
}