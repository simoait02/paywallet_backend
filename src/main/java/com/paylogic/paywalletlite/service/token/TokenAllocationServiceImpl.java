package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.token.TokenDenomination;
import com.paylogic.paywalletlite.domain.token.enums.TokenAllocationConfigStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import com.paylogic.paywalletlite.dto.request.TokenAllocationConfigRequestDto;
import com.paylogic.paywalletlite.dto.request.TokenDenominationRequestDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.token.TokenAllocationConfigRepository;
import com.paylogic.paywalletlite.repository.token.TokenDenominationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TokenAllocationServiceImpl implements TokenAllocationService {

    private final TokenAllocationConfigRepository configRepository;
    private final TokenDenominationRepository denominationRepository;
    private final WalletService walletService;

    @Autowired
    public TokenAllocationServiceImpl(TokenAllocationConfigRepository configRepository,
                                            TokenDenominationRepository denominationRepository,
                                            WalletService walletService) {
        this.configRepository = configRepository;
        this.denominationRepository = denominationRepository;
        this.walletService = walletService;
    }

    // ============================================================
    // CONFIGURATION
    // ============================================================

    @Override
    @Transactional
    public TokenAllocationConfig createConfig(TokenAllocationConfigRequestDto request, String createdBy) throws BusinessException {
        if (configRepository.existsByName(request.getConfigName())) {
            throw new BusinessException("Config name already exists: " + request.getConfigName());
        }

        // Récupérer les dénominations
        Set<TokenDenomination> denominations = new HashSet<TokenDenomination>();
        for (UUID denominationId : request.getDenominationIds()) {
            TokenDenomination denom = denominationRepository.findById(denominationId)
                    .orElseThrow(() -> new BusinessException("Denomination not found: " + denominationId));
            if (!Boolean.TRUE.equals(denom.getIsActive())) {
                throw new BusinessException("Denomination is not active: " + denominationId);
            }
            denominations.add(denom);
        }

        TokenAllocationConfig config = new TokenAllocationConfig();
        config.setConfigName(request.getConfigName());
        config.setWalletType(request.getWalletType());
        config.setDensityThreshold(request.getDensityThreshold());
        config.setSlidingWindowSize(request.getSlidingWindowSize());
        config.setMaxTokenCount(request.getMaxTokenCount());
        config.setMinSingleTokenValue(request.getMinSingleTokenValue());
        config.setMaxSingleTokenValue(request.getMaxSingleTokenValue());
        config.setMaxTransfersPerToken(request.getMaxTransfersPerToken());
        config.setTokenLifetimeHours(request.getTokenLifetimeHours());
        config.setAllowOverpayment(request.getAllowOverpayment() != null ? request.getAllowOverpayment() : false);
        config.setMaxOverpaymentThreshold(request.getMaxOverpaymentThreshold());
        config.setStatus(TokenAllocationConfigStatus.ACTIVE);
        config.setCreatedBy(createdBy);
        config.setDenominations(denominations);

        return configRepository.save(config);
    }

    @Override
    @Transactional
    public TokenAllocationConfig updateConfig(UUID configId, TokenAllocationConfigRequestDto request) throws BusinessException {
        TokenAllocationConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new BusinessException("Config not found: " + configId));

        // Mise à jour des champs
        config.setDensityThreshold(request.getDensityThreshold());
        config.setSlidingWindowSize(request.getSlidingWindowSize());
        config.setMaxTokenCount(request.getMaxTokenCount());
        config.setMinSingleTokenValue(request.getMinSingleTokenValue());
        config.setMaxSingleTokenValue(request.getMaxSingleTokenValue());
        config.setMaxTransfersPerToken(request.getMaxTransfersPerToken());
        config.setTokenLifetimeHours(request.getTokenLifetimeHours());
        config.setAllowOverpayment(request.getAllowOverpayment() != null ? request.getAllowOverpayment() : false);
        config.setMaxOverpaymentThreshold(request.getMaxOverpaymentThreshold());
        config.setUpdatedAt(LocalDateTime.now());

        // Mise à jour des dénominations si fournies
        if (request.getDenominationIds() != null && !request.getDenominationIds().isEmpty()) {
            Set<TokenDenomination> newDenominations = new HashSet<TokenDenomination>();
            for (UUID denominationId : request.getDenominationIds()) {
                TokenDenomination denom = denominationRepository.findById(denominationId)
                        .orElseThrow(() -> new BusinessException("Denomination not found: " + denominationId));
                newDenominations.add(denom);
            }
            config.setDenominations(newDenominations);
        }

        return configRepository.save(config);
    }

    @Override
    public TokenAllocationConfig findConfigById(UUID configId) throws BusinessException {
        return configRepository.findById(configId)
                .orElseThrow(() -> new BusinessException("Config not found: " + configId));
    }

    @Override
    public List<TokenAllocationConfig> findAllActiveConfigs() {
        return configRepository.findAllActive();
    }

    @Override
    public List<TokenAllocationConfig> findConfigsByWalletType(WalletType walletType) {
        return configRepository.findByWalletType(walletType);
    }

    @Override
    @Transactional
    public void activateConfig(UUID configId) throws BusinessException {
        TokenAllocationConfig config = findConfigById(configId);
        config.setStatus(TokenAllocationConfigStatus.ACTIVE);
        configRepository.save(config);
    }

    @Override
    @Transactional
    public void deprecateConfig(UUID configId) throws BusinessException {
        TokenAllocationConfig config = findConfigById(configId);
        config.setStatus(TokenAllocationConfigStatus.DEPRECATED);
        configRepository.save(config);
    }

    // ============================================================
    // DENOMINATIONS
    // ============================================================

    @Override
    @Transactional
    public TokenDenomination createDenomination(TokenDenominationRequestDto request) throws BusinessException {
        if (denominationRepository.existsByValueAndCurrencyCode(request.getValue(), request.getCurrencyCode())) {
            throw new BusinessException("Denomination already exists for value "
                    + request.getValue() + " " + request.getCurrencyCode());
        }

        TokenDenomination denomination = new TokenDenomination();
        denomination.setValue(request.getValue());
        denomination.setCurrencyCode(request.getCurrencyCode());
        denomination.setIsActive(true);
        denomination.setPriorityOrder(request.getPriorityOrder());
        denomination.setDensityWeight(request.getDensityWeight());
        denomination.setMinAllocationAmount(request.getMinAllocationAmount());
        denomination.setMaxAllocationAmount(request.getMaxAllocationAmount());

        return denominationRepository.save(denomination);
    }

    @Override
    @Transactional
    public TokenDenomination updateDenomination(UUID denominationId, TokenDenominationRequestDto request) throws BusinessException {
        TokenDenomination denomination = denominationRepository.findById(denominationId)
                .orElseThrow(() -> new BusinessException("Denomination not found: " + denominationId));

        denomination.setPriorityOrder(request.getPriorityOrder());
        denomination.setDensityWeight(request.getDensityWeight());
        denomination.setMinAllocationAmount(request.getMinAllocationAmount());
        denomination.setMaxAllocationAmount(request.getMaxAllocationAmount());
        denomination.setUpdatedAt(LocalDateTime.now());

        return denominationRepository.save(denomination);
    }

    @Override
    public List<TokenDenomination> findAllActiveDenominations() {
        return denominationRepository.findAllActive();
    }

    @Override
    public List<TokenDenomination> findDenominationsByCurrencyCode(String currencyCode) {
        return denominationRepository.findByCurrencyCode(currencyCode);
    }

    @Override
    @Transactional
    public void activateDenomination(UUID denominationId) throws BusinessException {
        TokenDenomination denomination = denominationRepository.findById(denominationId)
                .orElseThrow(() -> new BusinessException("Denomination not found: " + denominationId));
        denomination.setIsActive(true);
        denominationRepository.save(denomination);
    }

    @Override
    @Transactional
    public void deactivateDenomination(UUID denominationId) throws BusinessException {
        TokenDenomination denomination = denominationRepository.findById(denominationId)
                .orElseThrow(() -> new BusinessException("Denomination not found: " + denominationId));
        denomination.setIsActive(false);
        denominationRepository.save(denomination);
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    @Override
    public TokenAllocationConfig resolveConfigForWallet(WalletType walletType) throws BusinessException {
        List<TokenAllocationConfig> configs = configRepository.findByWalletType(walletType);

        for (TokenAllocationConfig config : configs) {
            if (config.getStatus() == TokenAllocationConfigStatus.ACTIVE) {
                return config;
            }
        }

        throw new BusinessException("No active allocation config found for wallet type: " + walletType);
    }

    @Override
    public List<BigDecimal> getSortedDenominationValues(UUID configId) throws BusinessException {
        TokenAllocationConfig config = findConfigById(configId);
        return config.getSortedDenominationValues();
    }

    @Override
    public boolean canAllocate(UUID walletId, BigDecimal amount) {
        try {
            Wallet wallet = walletService.findById(walletId);
            return wallet.getOnlineBalance().compareTo(amount) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}