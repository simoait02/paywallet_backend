package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TokenRedemptionService {

    /**
     * Redeem un token unique.
     * Convertit la valeur du token en solde online du payee.
     */
    Token redeemToken(UUID tokenId, UUID redeemerWalletId, String validationSignature) throws BusinessException;

    /**
     * Redeem un batch de tokens.
     */
    List<Token> redeemTokens(List<UUID> tokenIds, UUID redeemerWalletId) throws BusinessException;

    /**
     * Calcule la valeur totale d'une liste de tokens.
     */
    BigDecimal calculateRedemptionValue(List<UUID> tokenIds) throws BusinessException;

    /**
     * Vérifie si un token est éligible au redemption.
     */
    boolean isEligibleForRedemption(UUID tokenId, UUID walletId);
}