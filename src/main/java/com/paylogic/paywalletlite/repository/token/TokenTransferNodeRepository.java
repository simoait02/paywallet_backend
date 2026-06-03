package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenTransferNode;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenTransferNodeRepository {
    TokenTransferNode save(TokenTransferNode node);
    Optional<TokenTransferNode> findById(UUID transferNodeId);
    List<TokenTransferNode> findByTokenId(UUID tokenId);
    List<TokenTransferNode> findByTokenIdOrdered(UUID tokenId);
    List<TokenTransferNode> findByPayerWalletId(UUID walletId);
    List<TokenTransferNode> findByPayeeWalletId(UUID walletId);
    List<TokenTransferNode> findByPayerOrPayeeWalletId(UUID walletId);
    Optional<TokenTransferNode> findLatestByTokenId(UUID tokenId);
    Optional<TokenTransferNode> findFirstByTokenId(UUID tokenId); // Émission initiale
    long countByTokenId(UUID tokenId);
    boolean existsByTransferHash(String transferHash);
    void delete(TokenTransferNode node);
}