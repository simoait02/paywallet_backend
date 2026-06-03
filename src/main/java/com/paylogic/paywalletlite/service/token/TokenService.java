package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.dto.request.TokenAllocationRequestDto;
import com.paylogic.paywalletlite.dto.response.TokenResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.exception.DoubleSpendException;
import com.paylogic.paywalletlite.exception.InsufficientFundsException;
import com.paylogic.paywalletlite.exception.TokenExpiredException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TokenService {
    // Allocation
    List<Token> allocateTokens(UUID walletId, TokenAllocationRequestDto request) throws BusinessException;
    List<Token> allocateTokensForOffline(UUID walletId, BigDecimal amount) throws BusinessException;

    // Query
    Token findById(UUID tokenId);
    List<Token> findByWalletId(UUID walletId);
    List<Token> findByWalletIdAndStatus(UUID walletId, TokenStatus status);
    Token findByNonce(String nonce);

    // Transfer (Offline P2P)
    TokenTransferNode transferToken(UUID tokenId, UUID fromWalletId, UUID toWalletId, String payerSignature, LocalDateTime transferTimestamp)
            throws BusinessException, DoubleSpendException, TokenExpiredException;

    // Validation
    boolean validateTokenForOfflinePayment(UUID tokenId, UUID walletId) throws BusinessException;
    boolean verifyTokenSignature(UUID tokenId, String signature) throws BusinessException;

    // Lifecycle
    void expireToken(UUID tokenId);
    void revokeToken(UUID tokenId, String reason);
    void cleanupExpiredTokens();

    // Statistics
    long countActiveTokensByWallet(UUID walletId);
    BigDecimal getTotalValueByWallet(UUID walletId);
}