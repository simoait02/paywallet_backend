package com.paylogic.paywalletlite.repository.wallet;

import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class WalletConfigRepositoryImpl implements WalletConfigRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public WalletConfig save(WalletConfig config) {
        if (config.getConfigId() == null) {
            entityManager.persist(config);
            return config;
        }
        return entityManager.merge(config);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletConfig> findById(UUID configId) {
        return Optional.ofNullable(entityManager.find(WalletConfig.class, configId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> findByStatus(WalletConfigStatus status) {
        TypedQuery<WalletConfig> query = entityManager.createQuery(
                "SELECT wc FROM WalletConfig wc WHERE wc.status = :status ORDER BY wc.createdAt DESC",
                WalletConfig.class
        );
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> findAll() {
        TypedQuery<WalletConfig> query = entityManager.createQuery(
                "SELECT wc FROM WalletConfig wc ORDER BY wc.walletType ASC, wc.createdAt DESC",
                WalletConfig.class
        );
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> findByWalletType(WalletType walletType) {
        TypedQuery<WalletConfig> query = entityManager.createQuery(
                "SELECT wc FROM WalletConfig wc WHERE wc.walletType = :walletType ORDER BY wc.createdAt DESC",
                WalletConfig.class
        );
        query.setParameter("walletType", walletType);
        return query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WalletConfig> findByWalletTypeAndStatus(WalletType walletType, WalletConfigStatus status) {
        TypedQuery<WalletConfig> query = entityManager.createQuery(
                "SELECT wc FROM WalletConfig wc WHERE wc.walletType = :walletType AND wc.status = :status ORDER BY wc.createdAt DESC",
                WalletConfig.class
        );
        query.setParameter("walletType", walletType);
        query.setParameter("status", status);
        return query.getResultList();
    }
}