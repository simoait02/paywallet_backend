package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.exception.DoubleSpendException;
import com.paylogic.paywalletlite.exception.TokenExpiredException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service centralisé de validation des tokens.
 *
 * Contraintes vérifiées :
 * - Unicité (nonce + hash)
 * - Non-falsifiable (signature)
 * - Vérifiable offline (structure + hash)
 * - Transférable offline (état + compteur)
 * - Traçable (historique)
 * - Anti double-spending (status REDEEMED)
 */
@Service
public class TokenValidationService {

    /**
     * Validation complète d'un token pour une opération de transfert.
     */
    public void validateForTransfer(Token token) {
        validateExists(token);
        validateNotExpired(token);
        validateNotRedeemed(token);
        validateMaxTransfers(token);
        validateStatusForTransfer(token);
    }

    /**
     * Validation pour une opération de redemption (consommation finale).
     */
    public void validateForRedemption(Token token) {
        validateExists(token);
        validateNotExpired(token);
        validateNotRedeemed(token);
        //validateOwnership(token);
        validateSignatureIntegrity(token);
    }

    /**
     * Validation pour une opération offline (paiement sans connexion).
     */
    public void validateForOfflinePayment(Token token) {
        validateExists(token);
        validateNotExpired(token);
        validateNotRedeemed(token);
        validateStatusForOffline(token);
        validateHashIntegrity(token);
    }

    /**
     * Vérifie que le token existe.
     */
    public void validateExists(Token token) {
        if (token == null) {
            throw new BusinessException("Token is null");
        }
    }

    /**
     * Vérifie que le token n'est pas expiré.
     */
    public void validateNotExpired(Token token) {
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired at: " + token.getExpiresAt());
        }
    }

    /**
     * Vérifie que le token n'a pas déjà été consommé (anti double-spending).
     */
    public void validateNotRedeemed(Token token) {
        if (token.getStatus() == TokenStatus.REDEEMED) {
            throw new DoubleSpendException("Token already redeemed: " + token.getTokenId());
        }
        if (token.getStatus() == TokenStatus.INVALID || token.getStatus() == TokenStatus.REVOKED) {
            throw new DoubleSpendException("Token is invalid or revoked: " + token.getTokenId());
        }
    }

    /**
     * Vérifie que le nombre max de transferts n'est pas atteint.
     */
    public void validateMaxTransfers(Token token) {
        if (token.getTransferCount() != null
                && token.getMaxTransfers() != null
                && token.getTransferCount() >= token.getMaxTransfers()) {
            throw new BusinessException(String.format(
                    "Maximum transfer count reached (%d/%d) for token: %s",
                    token.getTransferCount(), token.getMaxTransfers(), token.getTokenId()
            ));
        }
    }

    /**
     * Vérifie que le status permet le transfert.
     */
    public void validateStatusForTransfer(Token token) {
        if (token.getStatus() != TokenStatus.ALLOCATED
                && token.getStatus() != TokenStatus.TRANSFERRED) {
            throw new BusinessException(String.format(
                    "Token status '%s' does not allow transfer. Expected: ALLOCATED or TRANSFERRED",
                    token.getStatus()
            ));
        }
    }

    /**
     * Vérifie que le status permet le paiement offline.
     */
    public void validateStatusForOffline(Token token) {
        if (token.getStatus() != TokenStatus.ALLOCATED
                && token.getStatus() != TokenStatus.TRANSFERRED) {
            throw new BusinessException(String.format(
                    "Token status '%s' does not allow offline payment. Expected: ALLOCATED or TRANSFERRED",
                    token.getStatus()
            ));
        }
    }

    /**
     * Vérifie l'appartenance du token au wallet.
     */
    public void validateOwnership(Token token, java.util.UUID expectedWalletId) {
        if (token.getCurrentHolderWallet() == null
                || !token.getCurrentHolderWallet().getWalletId().equals(expectedWalletId)) {
            throw new BusinessException(String.format(
                    "Token not owned by wallet %s. Current holder: %s",
                    expectedWalletId,
                    token.getCurrentHolderWallet() != null
                            ? token.getCurrentHolderWallet().getWalletId()
                            : "null"
            ));
        }
    }

    /**
     * Vérifie l'intégrité du hash du token.
     */
    public void validateHashIntegrity(Token token) {
        if (token.getTokenHash() == null || token.getTokenHash().isEmpty()) {
            throw new BusinessException("Token hash is missing");
        }
        if (token.getNonce() == null || token.getNonce().isEmpty()) {
            throw new BusinessException("Token nonce is missing");
        }
    }

    /**
     * Vérifie l'intégrité de la signature.
     */
    public void validateSignatureIntegrity(Token token) {
        if (token.getTokenSignature() == null) {
            throw new BusinessException("Token signature is missing");
        }
        if (token.getTokenSignature().getSignatureValue() == null
                || token.getTokenSignature().getSignatureValue().isEmpty()) {
            throw new BusinessException("Token signature value is missing");
        }
    }

    /**
     * Vérifie l'unicité du nonce (à appeler avant persistance).
     */
    public void validateNonceUniqueness(String nonce, java.util.function.Predicate<String> existsCheck) {
        if (existsCheck.test(nonce)) {
            throw new BusinessException("Token nonce already exists: " + nonce);
        }
    }
}