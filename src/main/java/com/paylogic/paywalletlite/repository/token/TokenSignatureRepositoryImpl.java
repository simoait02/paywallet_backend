package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenSignature;
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
public class TokenSignatureRepositoryImpl implements TokenSignatureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public TokenSignature save(TokenSignature signature) {
        if (signature.getSignatureId() == null) {
            entityManager.persist(signature);
            return signature;
        }
        return entityManager.merge(signature);
    }

    @Override
    public Optional<TokenSignature> findById(UUID signatureId) {
        return Optional.ofNullable(entityManager.find(TokenSignature.class, signatureId));
    }

    @Override
    public Optional<TokenSignature> findByTokenId(UUID tokenId) {
        TypedQuery<TokenSignature> query = entityManager.createQuery(
                "SELECT s FROM TokenSignature s WHERE s.token.tokenId = :tid",
                TokenSignature.class);
        query.setParameter("tid", tokenId);
        try {
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TokenSignature> findByIssuerPublicKey(String issuerPublicKey) {
        return entityManager.createQuery(
                        "SELECT s FROM TokenSignature s WHERE s.issuerPublicKey = :pk",
                        TokenSignature.class)
                .setParameter("pk", issuerPublicKey)
                .getResultList();
    }

    @Override
    public List<TokenSignature> findBySignatureAlgorithm(String algorithm) {
        return entityManager.createQuery(
                        "SELECT s FROM TokenSignature s WHERE s.signatureAlgorithm = :algo",
                        TokenSignature.class)
                .setParameter("algo", algorithm)
                .getResultList();
    }

    @Override
    public boolean existsByTokenId(UUID tokenId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(s) FROM TokenSignature s WHERE s.token.tokenId = :tid", Long.class)
                .setParameter("tid", tokenId)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public void delete(TokenSignature signature) {
        entityManager.remove(entityManager.contains(signature) ? signature : entityManager.merge(signature));
    }

    @Override
    @Transactional
    public void deleteByTokenId(UUID tokenId) {
        entityManager.createQuery(
                        "DELETE FROM TokenSignature s WHERE s.token.tokenId = :tid")
                .setParameter("tid", tokenId)
                .executeUpdate();
    }
}