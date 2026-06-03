package com.paylogic.paywalletlite.mapper;

import com.paylogic.paywalletlite.domain.transaction.LedgerEntry;
import com.paylogic.paywalletlite.dto.request.LedgerEntryRequestDto;
import com.paylogic.paywalletlite.dto.response.LedgerEntryResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper pour LedgerEntry.
 */
@Component
public class LedgerEntryMapper {

    public LedgerEntry toEntity(LedgerEntryRequestDto dto) {
        if (dto == null) return null;

        LedgerEntry entry = new LedgerEntry();
        entry.setLedgerId(dto.getLedgerId());
        entry.setTransactionId(dto.getTransactionId());
        entry.setWalletId(dto.getWalletId());
        entry.setAmount(dto.getAmount());
        entry.setEntryType(dto.getEntryType());
        entry.setRecordedAt(LocalDateTime.now());
        entry.setPreviousEntryHash(dto.getPreviousEntryHash());

        return entry;
    }

    public LedgerEntryResponseDto toResponseDto(LedgerEntry entity) {
        if (entity == null) return null;

        LedgerEntryResponseDto dto = new LedgerEntryResponseDto();
        dto.setEntryId(entity.getEntryId());
        dto.setLedgerId(entity.getLedgerId());
        dto.setTransactionId(entity.getTransactionId());
        dto.setWalletId(entity.getWalletId());
        dto.setAmount(entity.getAmount());
        dto.setEntryType(entity.getEntryType());
        dto.setRecordedAt(entity.getRecordedAt());
        dto.setEntryHash(entity.getEntryHash());
        dto.setSequenceNumber(entity.getSequenceNumber());
        dto.setPreviousEntryHash(entity.getPreviousEntryHash());

        return dto;
    }
}