package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.token.enums.AllocationMode;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository {
    Token save(Token token);
    Optional<Token> findById(UUID tokenId);
    Optional<Token> findByNonce(String nonce);
    Optional<Token> findByTokenHash(String tokenHash);
    List<Token> findByHolderWalletId(UUID walletId);
    List<Token> findByHolderWalletIdAndStatus(UUID walletId, TokenStatus status);
    List<Token> findByOriginalWalletId(UUID walletId);
    List<Token> findByIssuerWalletId(UUID walletId);
    List<Token> findByStatus(TokenStatus status);
    List<Token> findExpiredTokens();
    List<Token> findByValue(BigDecimal value);
    List<Token> findByAllocationMode(AllocationMode mode);
    long countByHolderWalletIdAndStatus(UUID walletId, TokenStatus status);
    void updateStatus(UUID tokenId, TokenStatus status);
    void updateHolderWallet(UUID tokenId, UUID newHolderWalletId);
    void incrementTransferCount(UUID tokenId);
    void delete(Token token);
    void deleteExpiredTokens();
    boolean existsByNonce(String nonce);
    boolean existsById(UUID id);
}