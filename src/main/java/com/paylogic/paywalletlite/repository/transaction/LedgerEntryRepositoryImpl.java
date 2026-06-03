package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.LedgerEntry;
import com.paylogic.paywalletlite.domain.transaction.enums.EntryType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation JPA du repository LedgerEntry.
 */
@Repository
@Transactional(readOnly = true)
public class LedgerEntryRepositoryImpl implements LedgerEntryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public LedgerEntry save(LedgerEntry entry) {
        if (entry.getEntryId() == null) {
            entityManager.persist(entry);
            return entry;
        }
        return entityManager.merge(entry);
    }

    @Override
    public Optional<LedgerEntry> findById(UUID entryId) {
        return Optional.ofNullable(entityManager.find(LedgerEntry.class, entryId));
    }

    @Override
    public List<LedgerEntry> findByLedgerId(UUID ledgerId) {
        TypedQuery<LedgerEntry> query = entityManager.createQuery(
                "SELECT e FROM LedgerEntry e WHERE e.ledgerId = :ledgerId ORDER BY e.sequenceNumber ASC",
                LedgerEntry.class);
        query.setParameter("ledgerId", ledgerId);
        return query.getResultList();
    }

    @Override
    public List<LedgerEntry> findByTransactionId(UUID transactionId) {
        TypedQuery<LedgerEntry> query = entityManager.createQuery(
                "SELECT e FROM LedgerEntry e WHERE e.transactionId = :txId ORDER BY e.recordedAt ASC",
                LedgerEntry.class);
        query.setParameter("txId", transactionId);
        return query.getResultList();
    }

    @Override
    public List<LedgerEntry> findByWalletId(UUID walletId) {
        TypedQuery<LedgerEntry> query = entityManager.createQuery(
                "SELECT e FROM LedgerEntry e WHERE e.walletId = :walletId ORDER BY e.recordedAt DESC",
                LedgerEntry.class);
        query.setParameter("walletId", walletId);
        return query.getResultList();
    }

    @Override
    public List<LedgerEntry> findByEntryType(EntryType entryType) {
        TypedQuery<LedgerEntry> query = entityManager.createQuery(
                "SELECT e FROM LedgerEntry e WHERE e.entryType = :type ORDER BY e.recordedAt DESC",
                LedgerEntry.class);
        query.setParameter("type", entryType);
        return query.getResultList();
    }

    @Override
    public Optional<LedgerEntry> findLastEntryByLedgerId(UUID ledgerId) {
        TypedQuery<LedgerEntry> query = entityManager.createQuery(
                "SELECT e FROM LedgerEntry e WHERE e.ledgerId = :ledgerId ORDER BY e.sequenceNumber DESC",
                LedgerEntry.class);
        query.setParameter("ledgerId", ledgerId);
        query.setMaxResults(1);
        List<LedgerEntry> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public long countByLedgerId(UUID ledgerId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(e) FROM LedgerEntry e WHERE e.ledgerId = :ledgerId",
                Long.class);
        query.setParameter("ledgerId", ledgerId);
        return query.getSingleResult();
    }
}