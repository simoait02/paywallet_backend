package com.paylogic.paywalletlite.repository.wallet;

import com.paylogic.paywalletlite.domain.wallet.WalletKeyPair;
import com.paylogic.paywalletlite.domain.wallet.enums.KeyStatus;
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
public class WalletKeyPairRepositoryImpl implements WalletKeyPairRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public WalletKeyPair save(WalletKeyPair keyPair) {
        if (keyPair.getKeypairId() == null) {
            entityManager.persist(keyPair);
            return keyPair;
        }
        return entityManager.merge(keyPair);
    }

    @Override
    public Optional<WalletKeyPair> findById(UUID keypairId) {
        return Optional.ofNullable(entityManager.find(WalletKeyPair.class, keypairId));
    }

    @Override
    public List<WalletKeyPair> findByWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT kp FROM WalletKeyPair kp WHERE kp.wallet.walletId = :wid ORDER BY kp.createdAt DESC",
                        WalletKeyPair.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public Optional<WalletKeyPair> findActiveByWalletId(UUID walletId) {
        TypedQuery<WalletKeyPair> query = entityManager.createQuery(
                "SELECT kp FROM WalletKeyPair kp WHERE kp.wallet.walletId = :wid AND kp.status = :status",
                WalletKeyPair.class);
        query.setParameter("wid", walletId);
        query.setParameter("status", KeyStatus.ACTIVE);
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public void updateStatus(UUID keypairId, KeyStatus status) {
        entityManager.createQuery(
                        "UPDATE WalletKeyPair kp SET kp.status = :status WHERE kp.keypairId = :id")
                .setParameter("status", status)
                .setParameter("id", keypairId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void delete(WalletKeyPair keyPair) {
        entityManager.remove(entityManager.contains(keyPair) ? keyPair : entityManager.merge(keyPair));
    }
}