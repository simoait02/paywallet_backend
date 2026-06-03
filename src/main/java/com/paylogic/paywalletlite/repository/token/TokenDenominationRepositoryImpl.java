package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenDenomination;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class TokenDenominationRepositoryImpl implements TokenDenominationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TokenDenomination save(TokenDenomination denomination) {
        if (denomination.getDenominationId() == null) {
            entityManager.persist(denomination);
            return denomination;
        }
        return entityManager.merge(denomination);
    }

    @Override
    public Optional<TokenDenomination> findById(UUID denominationId) {
        return Optional.ofNullable(entityManager.find(TokenDenomination.class, denominationId));
    }

    @Override
    public Optional<TokenDenomination> findByValueAndCurrencyCode(BigDecimal value, String currencyCode) {
        TypedQuery<TokenDenomination> query = entityManager.createQuery(
                "SELECT d FROM TokenDenomination d WHERE d.value = :val AND d.currencyCode = :cc",
                TokenDenomination.class);
        query.setParameter("val", value);
        query.setParameter("cc", currencyCode);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TokenDenomination> findAllActive() {
        return entityManager.createQuery(
                        "SELECT d FROM TokenDenomination d WHERE d.isActive = true ORDER BY d.priorityOrder DESC",
                        TokenDenomination.class)
                .getResultList();
    }

    @Override
    public List<TokenDenomination> findByCurrencyCode(String currencyCode) {
        return entityManager.createQuery(
                        "SELECT d FROM TokenDenomination d WHERE d.currencyCode = :cc ORDER BY d.priorityOrder DESC",
                        TokenDenomination.class)
                .setParameter("cc", currencyCode)
                .getResultList();
    }

    @Override
    public List<TokenDenomination> findByCurrencyCodeOrdered(String currencyCode) {
        return findByCurrencyCode(currencyCode);
    }

    @Override
    @Transactional
    public void delete(TokenDenomination denomination) {
        entityManager.remove(entityManager.contains(denomination) ? denomination : entityManager.merge(denomination));
    }

    @Override
    public boolean existsByValueAndCurrencyCode(BigDecimal value, String currencyCode) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(d) FROM TokenDenomination d WHERE d.value = :val AND d.currencyCode = :cc", Long.class)
                .setParameter("val", value)
                .setParameter("cc", currencyCode)
                .getSingleResult();
        return count > 0;
    }
}