package com.paylogic.paywalletlite.repository.wallet;

import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WalletRepositoryImpl implements WalletRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Wallet save(Wallet wallet) {
        if (wallet.getWalletId() == null || entityManager.find(Wallet.class, wallet.getWalletId()) == null) {
            entityManager.persist(wallet);
            return wallet;
        } else {
            return entityManager.merge(wallet);
        }
    }

    @Override
    public Optional<Wallet> findById(UUID walletId) {
        Wallet wallet = entityManager.find(Wallet.class, walletId);
        return Optional.ofNullable(wallet);
    }

    @Override
    public List<Wallet> findByUserId(UUID userId) {
        TypedQuery<Wallet> query = entityManager.createQuery(
                "SELECT w FROM Wallet w WHERE w.userId = :userId", Wallet.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Wallet> findByUserIdAndStatus(UUID userId, WalletStatus status) {
        TypedQuery<Wallet> query = entityManager.createQuery(
                "SELECT w FROM Wallet w WHERE w.userId = :userId AND w.status = :status", Wallet.class);
        query.setParameter("userId", userId);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<Wallet> findByStatus(WalletStatus status) {
        TypedQuery<Wallet> query = entityManager.createQuery(
                "SELECT w FROM Wallet w WHERE w.status = :status", Wallet.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public boolean existsById(UUID walletId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(w) FROM Wallet w WHERE w.walletId = :walletId", Long.class)
                .setParameter("walletId", walletId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public long countByUserId(UUID userId) {
        return entityManager.createQuery(
                        "SELECT COUNT(w) FROM Wallet w WHERE w.userId = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Override
    public void delete(Wallet wallet) {
        if (entityManager.contains(wallet)) {
            entityManager.remove(wallet);
        } else {
            entityManager.remove(entityManager.merge(wallet));
        }
    }
}