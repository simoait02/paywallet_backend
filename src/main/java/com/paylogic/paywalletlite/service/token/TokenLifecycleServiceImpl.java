package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.exception.DoubleSpendException;
import com.paylogic.paywalletlite.exception.TokenExpiredException;
import com.paylogic.paywalletlite.repository.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TokenLifecycleServiceImpl implements TokenLifecycleService {

    // Graphe des transitions valides
    private static final Set<TokenStatus> CREATED_TRANSITIONS = EnumSet.of(TokenStatus.ALLOCATED, TokenStatus.INVALID);
    private static final Set<TokenStatus> ALLOCATED_TRANSITIONS = EnumSet.of(TokenStatus.TRANSFERRED, TokenStatus.EXPIRED, TokenStatus.REVOKED);
    private static final Set<TokenStatus> TRANSFERRED_TRANSITIONS = EnumSet.of(TokenStatus.TRANSFERRED, TokenStatus.EXPIRED, TokenStatus.REVOKED);
    private static final Set<TokenStatus> REDEEMED_TRANSITIONS = EnumSet.noneOf(TokenStatus.class);
    private static final Set<TokenStatus> EXPIRED_TRANSITIONS = EnumSet.noneOf(TokenStatus.class);
    private static final Set<TokenStatus> INVALID_TRANSITIONS = EnumSet.noneOf(TokenStatus.class);
    private static final Set<TokenStatus> REVOKED_TRANSITIONS = EnumSet.noneOf(TokenStatus.class);

    private final TokenRepository tokenRepository;
    private final TokenValidationService validationService;

    @Autowired
    public TokenLifecycleServiceImpl(TokenRepository tokenRepository,
                                     TokenValidationService validationService) {
        this.tokenRepository = tokenRepository;
        this.validationService = validationService;
    }

    @Override
    @Transactional
    public Token allocate(UUID tokenId) throws BusinessException {
        Token token = findToken(tokenId);
        validateStatusTransition(token.getStatus(), TokenStatus.ALLOCATED);

        token.setStatus(TokenStatus.ALLOCATED);
        token.setIssuedAt(LocalDateTime.now());

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token transfer(UUID tokenId, UUID fromWalletId, UUID toWalletId) throws BusinessException {
        Token token = findToken(tokenId);

        // Validation métier
        validationService.validateForTransfer(token);
        validationService.validateOwnership(token, fromWalletId);

        validateStatusTransition(token.getStatus(), TokenStatus.TRANSFERRED);

        token.incrementTransferCount();
        token.setStatus(TokenStatus.TRANSFERRED);

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token markPendingRedemption(UUID tokenId) throws BusinessException {
        Token token = findToken(tokenId);
        validateStatusTransition(token.getStatus(), TokenStatus.TRANSFERRED);

        token.setStatus(TokenStatus.TRANSFERRED);

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token redeem(UUID tokenId) throws BusinessException {
        Token token = findToken(tokenId);

        // Validation complète avant redemption
        validationService.validateForRedemption(token);

        // Transition depuis PENDING_REDEMPTION ou directement depuis ALLOCATED/TRANSFERRED
        if (token.getStatus() == TokenStatus.ALLOCATED) {
            token.markAsRedeemed();
        } else {
            validateStatusTransition(token.getStatus(), TokenStatus.REDEEMED);
            token.markAsRedeemed();
        }

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token expire(UUID tokenId) throws BusinessException {
        Token token = findToken(tokenId);

        if (token.getExpiresAt() != null && token.getExpiresAt().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Token has not expired yet. Expires at: " + token.getExpiresAt());
        }

        validateStatusTransition(token.getStatus(), TokenStatus.EXPIRED);
        token.markAsExpired();

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token revoke(UUID tokenId, String reason) throws BusinessException {
        Token token = findToken(tokenId);
        validateStatusTransition(token.getStatus(), TokenStatus.REVOKED);
        token.markAsRevoked();

        // TODO: Log de revocation avec raison (AuditLog)

        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token invalidate(UUID tokenId, String reason) throws BusinessException {
        Token token = findToken(tokenId);
        validateStatusTransition(token.getStatus(), TokenStatus.INVALID);
        token.setStatus(TokenStatus.INVALID);

        // TODO: Log d'invalidation avec raison (FraudAlert + AuditLog)

        return tokenRepository.save(token);
    }

    @Override
    public boolean canTransitionTo(Token token, TokenStatus newStatus) {
        try {
            validateStatusTransition(token.getStatus(), newStatus);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

    @Override
    public void validateStatusTransition(TokenStatus current, TokenStatus next) throws BusinessException {
        Set<TokenStatus> allowedTransitions = getAllowedTransitions(current);

        if (!allowedTransitions.contains(next)) {
            throw new BusinessException(String.format(
                    "Invalid status transition: %s → %s. Allowed: %s",
                    current, next, allowedTransitions
            ));
        }
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private Token findToken(UUID tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));
    }

    private Set<TokenStatus> getAllowedTransitions(TokenStatus status) {
        switch (status) {
            case CREATED:
                return CREATED_TRANSITIONS;
            case ALLOCATED:
                return ALLOCATED_TRANSITIONS;
            case TRANSFERRED:
                return TRANSFERRED_TRANSITIONS;
            case REDEEMED:
                return REDEEMED_TRANSITIONS;
            case EXPIRED:
                return EXPIRED_TRANSITIONS;
            case INVALID:
                return INVALID_TRANSITIONS;
            case REVOKED:
                return REVOKED_TRANSITIONS;
            default:
                throw new IllegalArgumentException("Unknown status: " + status);
        }
    }
}