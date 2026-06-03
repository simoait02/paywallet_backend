package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.enums.AllocationMode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class TokenRepositoryImpl implements TokenRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Token save(Token token) {
        if (token.getTokenId() == null) {
            entityManager.persist(token);
            return token;
        }
        return entityManager.merge(token);
    }

    @Override
    public Optional<Token> findById(UUID tokenId) {
        return Optional.ofNullable(entityManager.find(Token.class, tokenId));
    }

    @Override
    public Optional<Token> findByNonce(String nonce) {
        TypedQuery<Token> query = entityManager.createQuery(
                "SELECT t FROM Token t WHERE t.nonce = :nonce", Token.class);
        query.setParameter("nonce", nonce);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Token> findByTokenHash(String tokenHash) {
        TypedQuery<Token> query = entityManager.createQuery(
                "SELECT t FROM Token t WHERE t.tokenHash = :hash", Token.class);
        query.setParameter("hash", tokenHash);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Token> findByHolderWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.currentHolderWallet.walletId = :wid", Token.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public List<Token> findByHolderWalletIdAndStatus(UUID walletId, TokenStatus status) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.currentHolderWallet.walletId = :wid AND t.status = :status", Token.class)
                .setParameter("wid", walletId)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Token> findByOriginalWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.originalWallet.walletId = :wid", Token.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public List<Token> findByIssuerWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.issuerWallet.walletId = :wid", Token.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public List<Token> findByStatus(TokenStatus status) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.status = :status", Token.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public List<Token> findExpiredTokens() {
        List<TokenStatus> finalStatuses = Arrays.asList(
                TokenStatus.REDEEMED,
                TokenStatus.REVOKED,
                TokenStatus.INVALID
        );

        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.expiresAt < :now AND t.status NOT IN (:finalStatuses)",
                        Token.class)
                .setParameter("now", LocalDateTime.now())
                .setParameter("finalStatuses", finalStatuses)
                .getResultList();
    }

    @Override
    public List<Token> findByValue(BigDecimal value) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.value = :value", Token.class)
                .setParameter("value", value)
                .getResultList();
    }

    @Override
    public List<Token> findByAllocationMode(AllocationMode mode) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.allocationMode = :mode", Token.class)
                .setParameter("mode", mode)
                .getResultList();
    }

    @Override
    public long countByHolderWalletIdAndStatus(UUID walletId, TokenStatus status) {
        return entityManager.createQuery(
                        "SELECT COUNT(t) FROM Token t WHERE t.currentHolderWallet.walletId = :wid AND t.status = :status", Long.class)
                .setParameter("wid", walletId)
                .setParameter("status", status)
                .getSingleResult();
    }

    @Override
    @Transactional
    public void updateStatus(UUID tokenId, TokenStatus status) {
        entityManager.createQuery(
                        "UPDATE Token t SET t.status = :status WHERE t.tokenId = :id")
                .setParameter("status", status)
                .setParameter("id", tokenId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateHolderWallet(UUID tokenId, UUID newHolderWalletId) {
        entityManager.createQuery(
                        "UPDATE Token t SET t.currentHolderWallet.walletId = :wid, t.lastTransferAt = :now, t.transferCount = t.transferCount + 1 WHERE t.tokenId = :id")
                .setParameter("wid", newHolderWalletId)
                .setParameter("now", LocalDateTime.now())
                .setParameter("id", tokenId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void incrementTransferCount(UUID tokenId) {
        entityManager.createQuery(
                        "UPDATE Token t SET t.transferCount = t.transferCount + 1 WHERE t.tokenId = :id")
                .setParameter("id", tokenId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void delete(Token token) {
        entityManager.remove(entityManager.contains(token) ? token : entityManager.merge(token));
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        entityManager.createQuery(
                        "DELETE FROM Token t WHERE t.expiresAt < :now AND t.status = :status")
                .setParameter("now", LocalDateTime.now().minusDays(30))
                .setParameter("status", TokenStatus.EXPIRED)
                .executeUpdate();
    }

    @Override
    public boolean existsByNonce(String nonce) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(t) FROM Token t WHERE t.nonce = :nonce", Long.class)
                .setParameter("nonce", nonce)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsById(UUID id) {
        if (findById(id) == null) return false;
        return true;
    }
}