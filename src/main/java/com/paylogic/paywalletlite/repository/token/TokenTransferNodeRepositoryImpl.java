package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
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
public class TokenTransferNodeRepositoryImpl implements TokenTransferNodeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TokenTransferNode save(TokenTransferNode node) {
        if (node.getTransferNodeId() == null) {
            entityManager.persist(node);
            return node;
        }
        return entityManager.merge(node);
    }

    @Override
    public Optional<TokenTransferNode> findById(UUID transferNodeId) {
        return Optional.ofNullable(entityManager.find(TokenTransferNode.class, transferNodeId));
    }

    @Override
    public List<TokenTransferNode> findByTokenId(UUID tokenId) {
        return entityManager.createQuery(
                        "SELECT n FROM TokenTransferNode n WHERE n.token.tokenId = :tid",
                        TokenTransferNode.class)
                .setParameter("tid", tokenId)
                .getResultList();
    }

    @Override
    public List<TokenTransferNode> findByTokenIdOrdered(UUID tokenId) {
        return entityManager.createQuery(
                        "SELECT n FROM TokenTransferNode n WHERE n.token.tokenId = :tid ORDER BY n.transferTimestamp ASC",
                        TokenTransferNode.class)
                .setParameter("tid", tokenId)
                .getResultList();
    }

    @Override
    public List<TokenTransferNode> findByPayerWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT n FROM TokenTransferNode n WHERE n.payerWallet.walletId = :wid ORDER BY n.transferTimestamp DESC",
                        TokenTransferNode.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public List<TokenTransferNode> findByPayeeWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT n FROM TokenTransferNode n WHERE n.payeeWallet.walletId = :wid ORDER BY n.transferTimestamp DESC",
                        TokenTransferNode.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public List<TokenTransferNode> findByPayerOrPayeeWalletId(UUID walletId) {
        return entityManager.createQuery(
                        "SELECT n FROM TokenTransferNode n WHERE n.payerWallet.walletId = :wid OR n.payeeWallet.walletId = :wid ORDER BY n.transferTimestamp DESC",
                        TokenTransferNode.class)
                .setParameter("wid", walletId)
                .getResultList();
    }

    @Override
    public Optional<TokenTransferNode> findLatestByTokenId(UUID tokenId) {
        TypedQuery<TokenTransferNode> query = entityManager.createQuery(
                "SELECT n FROM TokenTransferNode n WHERE n.token.tokenId = :tid ORDER BY n.transferTimestamp DESC",
                TokenTransferNode.class);
        query.setParameter("tid", tokenId);
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<TokenTransferNode> findFirstByTokenId(UUID tokenId) {
        TypedQuery<TokenTransferNode> query = entityManager.createQuery(
                "SELECT n FROM TokenTransferNode n WHERE n.token.tokenId = :tid ORDER BY n.transferTimestamp ASC",
                TokenTransferNode.class);
        query.setParameter("tid", tokenId);
        query.setMaxResults(1);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public long countByTokenId(UUID tokenId) {
        return entityManager.createQuery(
                        "SELECT COUNT(n) FROM TokenTransferNode n WHERE n.token.tokenId = :tid", Long.class)
                .setParameter("tid", tokenId)
                .getSingleResult();
    }

    @Override
    public boolean existsByTransferHash(String transferHash) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(n) FROM TokenTransferNode n WHERE n.transferHash = :hash", Long.class)
                .setParameter("hash", transferHash)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void delete(TokenTransferNode node) {
        entityManager.remove(entityManager.contains(node) ? node : entityManager.merge(node));
    }
}