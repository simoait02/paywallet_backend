package com.paylogic.paywalletlite.repository.transaction;

import com.paylogic.paywalletlite.domain.transaction.LedgerEntry;
import com.paylogic.paywalletlite.domain.transaction.enums.EntryType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository pour les écritures comptables (LedgerEntry).
 */
public interface LedgerEntryRepository {

    LedgerEntry save(LedgerEntry entry);

    Optional<LedgerEntry> findById(UUID entryId);

    List<LedgerEntry> findByLedgerId(UUID ledgerId);

    List<LedgerEntry> findByTransactionId(UUID transactionId);

    List<LedgerEntry> findByWalletId(UUID walletId);

    List<LedgerEntry> findByEntryType(EntryType entryType);

    Optional<LedgerEntry> findLastEntryByLedgerId(UUID ledgerId);

    long countByLedgerId(UUID ledgerId);
}