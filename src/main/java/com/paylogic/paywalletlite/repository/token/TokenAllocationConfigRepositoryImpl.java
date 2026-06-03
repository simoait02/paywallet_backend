package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.token.enums.TokenAllocationConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
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
public class TokenAllocationConfigRepositoryImpl implements TokenAllocationConfigRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TokenAllocationConfig save(TokenAllocationConfig config) {
        if (config.getConfigId() == null) {
            entityManager.persist(config);
            return config;
        }
        return entityManager.merge(config);
    }

    @Override
    public Optional<TokenAllocationConfig> findById(UUID configId) {
        return Optional.ofNullable(entityManager.find(TokenAllocationConfig.class, configId));
    }

    @Override
    public Optional<TokenAllocationConfig> findByName(String configName) {
        TypedQuery<TokenAllocationConfig> query = entityManager.createQuery(
                "SELECT c FROM TokenAllocationConfig c WHERE c.configName = :name", TokenAllocationConfig.class);
        query.setParameter("name", configName);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TokenAllocationConfig> findByWalletType(WalletType walletType) {
        return entityManager.createQuery(
                        "SELECT c FROM TokenAllocationConfig c WHERE c.walletType = :type AND c.status = :status",
                        TokenAllocationConfig.class)
                .setParameter("type", walletType)
                .setParameter("status", TokenAllocationConfigStatus.ACTIVE)
                .getResultList();
    }

    @Override
    public List<TokenAllocationConfig> findAllActive() {
        return entityManager.createQuery(
                        "SELECT c FROM TokenAllocationConfig c WHERE c.status = :status",
                        TokenAllocationConfig.class)
                .setParameter("status", TokenAllocationConfigStatus.ACTIVE)
                .getResultList();
    }

    @Override
    @Transactional
    public void delete(TokenAllocationConfig config) {
        entityManager.remove(entityManager.contains(config) ? config : entityManager.merge(config));
    }

    @Override
    public boolean existsByName(String configName) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM TokenAllocationConfig c WHERE c.configName = :name", Long.class)
                .setParameter("name", configName)
                .getSingleResult();
        return count > 0;
    }
}