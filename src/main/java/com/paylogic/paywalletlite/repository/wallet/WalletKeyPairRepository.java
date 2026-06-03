package com.paylogic.paywalletlite.repository.wallet;

import com.paylogic.paywalletlite.domain.wallet.WalletKeyPair;
import com.paylogic.paywalletlite.domain.wallet.enums.KeyStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletKeyPairRepository {
    WalletKeyPair save(WalletKeyPair keyPair);
    Optional<WalletKeyPair> findById(UUID keypairId);
    List<WalletKeyPair> findByWalletId(UUID walletId);
    Optional<WalletKeyPair> findActiveByWalletId(UUID walletId);
    void updateStatus(UUID keypairId, KeyStatus status);
    void delete(WalletKeyPair keyPair);
}