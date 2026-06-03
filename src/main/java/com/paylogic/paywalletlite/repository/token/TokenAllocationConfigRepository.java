package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenAllocationConfigRepository {
    TokenAllocationConfig save(TokenAllocationConfig config);
    Optional<TokenAllocationConfig> findById(UUID configId);
    Optional<TokenAllocationConfig> findByName(String configName);
    List<TokenAllocationConfig> findByWalletType(WalletType walletType);
    List<TokenAllocationConfig> findAllActive();
    void delete(TokenAllocationConfig config);
    boolean existsByName(String configName);
}