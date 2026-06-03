package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.token.TokenDenomination;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import com.paylogic.paywalletlite.dto.request.TokenAllocationConfigRequestDto;
import com.paylogic.paywalletlite.dto.request.TokenDenominationRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TokenAllocationService {

    // === CONFIGURATION ===
    TokenAllocationConfig createConfig(TokenAllocationConfigRequestDto request, String createdBy) throws BusinessException;
    TokenAllocationConfig updateConfig(UUID configId, TokenAllocationConfigRequestDto request) throws BusinessException;
    TokenAllocationConfig findConfigById(UUID configId) throws BusinessException;
    List<TokenAllocationConfig> findAllActiveConfigs();
    List<TokenAllocationConfig> findConfigsByWalletType(WalletType walletType);
    void activateConfig(UUID configId) throws BusinessException;
    void deprecateConfig(UUID configId) throws BusinessException;

    // === DENOMINATIONS ===
    TokenDenomination createDenomination(TokenDenominationRequestDto request) throws BusinessException;
    TokenDenomination updateDenomination(UUID denominationId, TokenDenominationRequestDto request) throws BusinessException;
    List<TokenDenomination> findAllActiveDenominations();
    List<TokenDenomination> findDenominationsByCurrencyCode(String currencyCode);
    void activateDenomination(UUID denominationId) throws BusinessException;
    void deactivateDenomination(UUID denominationId) throws BusinessException;

    // === UTILITAIRES ===
    TokenAllocationConfig resolveConfigForWallet(WalletType walletType) throws BusinessException;
    List<java.math.BigDecimal> getSortedDenominationValues(UUID configId) throws BusinessException;
    public boolean canAllocate(UUID walletId, BigDecimal amount) throws BusinessException;
}