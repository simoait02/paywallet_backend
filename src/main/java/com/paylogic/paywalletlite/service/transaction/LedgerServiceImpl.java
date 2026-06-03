package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.transaction.Ledger;
import com.paylogic.paywalletlite.domain.transaction.LedgerEntry;
import com.paylogic.paywalletlite.domain.transaction.enums.EntryType;
import com.paylogic.paywalletlite.domain.transaction.enums.LedgerType;
import com.paylogic.paywalletlite.dto.request.LedgerEntryRequestDto;
import com.paylogic.paywalletlite.dto.response.LedgerEntryResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.mapper.LedgerEntryMapper;
import com.paylogic.paywalletlite.repository.transaction.LedgerEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service Ledger.
 * Gère le grand livre avec comptabilité en partie double et chaînage de hash.
 */
@Service
@Transactional
public class LedgerServiceImpl implements LedgerService {

    private static final Logger logger = LoggerFactory.getLogger(LedgerServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final LedgerEntryRepository ledgerEntryRepository;
    private final LedgerEntryMapper ledgerEntryMapper;

    @Autowired
    public LedgerServiceImpl(LedgerEntryRepository ledgerEntryRepository,
                             LedgerEntryMapper ledgerEntryMapper) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.ledgerEntryMapper = ledgerEntryMapper;
    }

    @Override
    public Ledger createLedger(LedgerType type, String version) {
        Ledger ledger = new Ledger();
        ledger.setType(type);
        ledger.setLedgerVersion(version != null ? version : "1.0.0");
        ledger.setCreatedAt(LocalDateTime.now());
        ledger.setIsSealed(false);

        entityManager.persist(ledger);
        logger.info("Ledger créé: id={}, type={}", ledger.getLedgerId(), type);
        return ledger;
    }

    @Override
    public LedgerEntryResponseDto recordEntry(LedgerEntryRequestDto entryDto) {
        // Déterminer le numéro de séquence
        long count = ledgerEntryRepository.countByLedgerId(entryDto.getLedgerId());
        int sequenceNumber = (int) count + 1;

        // Récupérer le hash de l'entrée précédente pour chaînage
        Optional<LedgerEntry> lastEntry = ledgerEntryRepository.findLastEntryByLedgerId(entryDto.getLedgerId());
        String previousHash = lastEntry.map(LedgerEntry::getEntryHash).orElse("0");

        LedgerEntry entry = ledgerEntryMapper.toEntity(entryDto);
        entry.setSequenceNumber(sequenceNumber);
        entry.setPreviousEntryHash(previousHash);

        // Calculer le hash de l'entrée courante
        String entryHash = computeEntryHash(entry);
        entry.setEntryHash(entryHash);

        LedgerEntry saved = ledgerEntryRepository.save(entry);
        logger.info("Écriture comptable enregistrée: ledger={}, seq={}, type={}, amount={}",
                entryDto.getLedgerId(), sequenceNumber, entryDto.getEntryType(), entryDto.getAmount());

        return ledgerEntryMapper.toResponseDto(saved);
    }

    @Override
    public void recordDoubleEntry(UUID transactionId, UUID senderWalletId, UUID receiverWalletId,
                                  BigDecimal amount, String description) {
        logger.info("Enregistrement double écriture pour transaction: {}", transactionId);

        // Récupérer ou créer le ledger MASTER
        Ledger masterLedger = getOrCreateMasterLedger();

        // Écriture DÉBIT (sender)
        LedgerEntryRequestDto debitEntry = new LedgerEntryRequestDto();
        debitEntry.setLedgerId(masterLedger.getLedgerId());
        debitEntry.setTransactionId(transactionId);
        debitEntry.setWalletId(senderWalletId);
        debitEntry.setAmount(amount);
        debitEntry.setEntryType(EntryType.DEBIT);
        recordEntry(debitEntry);

        // Écriture CRÉDIT (receiver)
        LedgerEntryRequestDto creditEntry = new LedgerEntryRequestDto();
        creditEntry.setLedgerId(masterLedger.getLedgerId());
        creditEntry.setTransactionId(transactionId);
        creditEntry.setWalletId(receiverWalletId);
        creditEntry.setAmount(amount);
        creditEntry.setEntryType(EntryType.CREDIT);
        recordEntry(creditEntry);

        logger.info("Double écriture enregistrée: tx={}, amount={}", transactionId, amount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryResponseDto> getEntriesByLedgerId(UUID ledgerId) {
        return ledgerEntryRepository.findByLedgerId(ledgerId).stream()
                .map(ledgerEntryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LedgerEntryResponseDto> getEntriesByWalletId(UUID walletId) {
        return ledgerEntryRepository.findByWalletId(walletId).stream()
                .map(ledgerEntryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateWalletBalance(UUID walletId) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByWalletId(walletId);

        BigDecimal credits = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.CREDIT || e.getEntryType() == EntryType.RELEASE)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debits = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.DEBIT || e.getEntryType() == EntryType.HOLD)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return credits.subtract(debits);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyLedgerIntegrity(UUID ledgerId) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByLedgerId(ledgerId);

        String expectedPreviousHash = "0";
        for (LedgerEntry entry : entries) {
            // Vérifier le chaînage
            if (!expectedPreviousHash.equals(entry.getPreviousEntryHash())) {
                logger.error("Chaînage brisé à l'entrée {}: attendu={}, trouvé={}",
                        entry.getSequenceNumber(), expectedPreviousHash, entry.getPreviousEntryHash());
                return false;
            }

            // Vérifier le hash de l'entrée
            String computedHash = computeEntryHash(entry);
            if (!computedHash.equals(entry.getEntryHash())) {
                logger.error("Hash invalide à l'entrée {}: attendu={}, calculé={}",
                        entry.getSequenceNumber(), entry.getEntryHash(), computedHash);
                return false;
            }

            expectedPreviousHash = entry.getEntryHash();
        }

        logger.info("Intégrité du ledger {} vérifiée: {} entrées", ledgerId, entries.size());
        return true;
    }

    @Override
    public void sealLedger(UUID ledgerId) {
        Ledger ledger = entityManager.find(Ledger.class, ledgerId);
        if (ledger == null) {
            throw new BusinessException("Ledger introuvable: " + ledgerId);
        }
        ledger.setIsSealed(true);
        entityManager.merge(ledger);
        logger.info("Ledger scellé: {}", ledgerId);
    }

    // ============ Méthodes privées ============

    private Ledger getOrCreateMasterLedger() {
        // Rechercher un ledger MASTER existant
        javax.persistence.TypedQuery<Ledger> query = entityManager.createQuery(
                "SELECT l FROM Ledger l WHERE l.type = :type AND l.isSealed = false",
                Ledger.class);
        query.setParameter("type", LedgerType.MASTER);
        query.setMaxResults(1);

        List<Ledger> results = query.getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }

        // Créer un nouveau ledger MASTER
        return createLedger(LedgerType.MASTER, "1.0.0");
    }

    private String computeEntryHash(LedgerEntry entry) {
        String data = entry.getLedgerId().toString() +
                entry.getTransactionId().toString() +
                entry.getWalletId().toString() +
                entry.getAmount().toString() +
                entry.getEntryType().name() +
                entry.getRecordedAt().toString() +
                entry.getSequenceNumber() +
                entry.getPreviousEntryHash();

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            logger.error("Erreur de hash", e);
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}