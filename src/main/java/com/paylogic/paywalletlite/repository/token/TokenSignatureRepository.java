package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenSignature;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenSignatureRepository {
    TokenSignature save(TokenSignature signature);
    Optional<TokenSignature> findById(UUID signatureId);
    Optional<TokenSignature> findByTokenId(UUID tokenId);
    List<TokenSignature> findByIssuerPublicKey(String issuerPublicKey);
    List<TokenSignature> findBySignatureAlgorithm(String algorithm);
    boolean existsByTokenId(UUID tokenId);
    void delete(TokenSignature signature);
    void deleteByTokenId(UUID tokenId);
}