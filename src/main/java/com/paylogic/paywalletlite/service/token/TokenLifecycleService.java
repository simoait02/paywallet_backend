package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.UUID;

public interface TokenLifecycleService {

    // Transitions de status
    Token allocate(UUID tokenId) throws BusinessException;
    Token transfer(UUID tokenId, UUID fromWalletId, UUID toWalletId) throws BusinessException;
    Token markPendingRedemption(UUID tokenId) throws BusinessException;
    Token redeem(UUID tokenId) throws BusinessException;
    Token expire(UUID tokenId) throws BusinessException;
    Token revoke(UUID tokenId, String reason) throws BusinessException;
    Token invalidate(UUID tokenId, String reason) throws BusinessException;

    // Vérifications
    boolean canTransitionTo(Token token, TokenStatus newStatus);
    void validateStatusTransition(TokenStatus current, TokenStatus next) throws BusinessException;
}