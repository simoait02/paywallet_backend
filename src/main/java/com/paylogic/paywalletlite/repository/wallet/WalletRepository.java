package com.paylogic.paywalletlite.repository.wallet;

import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(UUID walletId);

    List<Wallet> findByUserId(UUID userId);

    List<Wallet> findByUserIdAndStatus(UUID userId, WalletStatus status);

    List<Wallet> findByStatus(WalletStatus status);

    boolean existsById(UUID walletId);

    long countByUserId(UUID userId);

    void delete(Wallet wallet);
}