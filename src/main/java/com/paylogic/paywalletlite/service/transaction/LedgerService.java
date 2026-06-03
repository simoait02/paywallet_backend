package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.transaction.Ledger;
import com.paylogic.paywalletlite.domain.transaction.LedgerEntry;
import com.paylogic.paywalletlite.domain.transaction.enums.EntryType;
import com.paylogic.paywalletlite.domain.transaction.enums.LedgerType;
import com.paylogic.paywalletlite.dto.request.LedgerEntryRequestDto;
import com.paylogic.paywalletlite.dto.response.LedgerEntryResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service métier pour la gestion du grand livre (Ledger).
 * Implémente la comptabilité en partie double.
 */
public interface LedgerService {

    /**
     * Crée un nouveau ledger.
     */
    Ledger createLedger(LedgerType type, String version);

    /**
     * Enregistre une écriture comptable (débit ou crédit).
     */
    LedgerEntryResponseDto recordEntry(LedgerEntryRequestDto entryDto);

    /**
     * Enregistre une paire d'écritures (débit + crédit) pour une transaction.
     */
    void recordDoubleEntry(UUID transactionId, UUID senderWalletId, UUID receiverWalletId,
                           BigDecimal amount, String description);

    /**
     * Récupère toutes les écritures d'un ledger.
     */
    List<LedgerEntryResponseDto> getEntriesByLedgerId(UUID ledgerId);

    /**
     * Récupère les écritures d'un wallet.
     */
    List<LedgerEntryResponseDto> getEntriesByWalletId(UUID walletId);

    /**
     * Calcule le solde d'un wallet à partir du ledger.
     */
    BigDecimal calculateWalletBalance(UUID walletId);

    /**
     * Vérifie l'intégrité de la chaîne de hash du ledger.
     */
    boolean verifyLedgerIntegrity(UUID ledgerId);

    /**
     * Scelle un ledger (immuable).
     */
    void sealLedger(UUID ledgerId);
}