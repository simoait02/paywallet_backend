package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.enums.OverpaymentStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.TransactionStatus;
import com.paylogic.paywalletlite.dto.request.TransactionCreateRequestDto;
import com.paylogic.paywalletlite.dto.response.TransactionResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.mapper.TransactionMapper;
import com.paylogic.paywalletlite.repository.transaction.TransactionRepository;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implémentation du service Transaction.
 * Gère le cycle de vie complet des transactions selon le modèle 3 phases.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletService walletService;
    private final OverpaymentHandler overpaymentHandler;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  TransactionMapper transactionMapper,
                                  WalletService walletService,
                                  OverpaymentHandler overpaymentHandler) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.walletService = walletService;
        this.overpaymentHandler = overpaymentHandler;
    }

    @Override
    public TransactionResponseDto createTransaction(TransactionCreateRequestDto request) {
        logger.info("Création d'une transaction: sender={}, receiver={}, amount={}, type={}, offline={}",
                request.getSenderWalletId(), request.getReceiverWalletId(),
                request.getRequestedAmount(), request.getType(), request.getIsOffline());

        // Validation: vérifier l'existence des wallets
        validateWalletsExist(request.getSenderWalletId(), request.getReceiverWalletId());

        // Validation: vérifier les fonds suffisants pour les transactions online
        if (!Boolean.TRUE.equals(request.getIsOffline())) {
            validateSufficientFunds(request.getSenderWalletId(), request.getRequestedAmount());
        }

        Transaction transaction = transactionMapper.toEntity(request);

        // Calcul du hash de transaction pour traçabilité
        String txHash = computeTransactionHash(transaction);
        transaction.setTransactionHash(txHash);

        Transaction saved = transactionRepository.save(transaction);

        logger.info("Transaction créée avec succès: id={}, hash={}", saved.getTransactionId(), txHash);

        return transactionMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDto getTransactionById(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));
        return transactionMapper.toResponseDto(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsByWalletId(UUID walletId) {
        List<Transaction> sent = transactionRepository.findBySenderWalletId(walletId);
        List<Transaction> received = transactionRepository.findByReceiverWalletId(walletId);

        sent.addAll(received);
        return sent.stream()
                .distinct()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status).stream()
                .map(transactionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponseDto updateTransactionStatus(UUID transactionId, TransactionStatus newStatus) {
        logger.info("Mise à jour du statut de la transaction {}: {} → {}", transactionId,
                getTransactionById(transactionId).getStatus(), newStatus);

        transactionRepository.updateStatus(transactionId, newStatus);

        Transaction updated = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée"));

        return transactionMapper.toResponseDto(updated);
    }

    @Override
    public TransactionResponseDto completeTransaction(UUID transactionId) {
        logger.info("Finalisation de la transaction: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));

        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setCompletedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);

        // Gestion du surpaiement si applicable
        if (transaction.getOverpaymentAmount() != null &&
                transaction.getOverpaymentAmount().compareTo(BigDecimal.ZERO) > 0) {
            overpaymentHandler.detectOverpayment(transaction, transaction.getTransferredAmount());
        }

        logger.info("Transaction finalisée: {}", transactionId);
        return transactionMapper.toResponseDto(saved);
    }

    @Override
    public TransactionResponseDto cancelTransaction(UUID transactionId) {
        logger.info("Annulation de la transaction: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));

        // Seules les transactions PENDING ou OFFLINE_PENDING peuvent être annulées
        if (transaction.getStatus() != TransactionStatus.PENDING &&
                transaction.getStatus() != TransactionStatus.OFFLINE_PENDING) {
            throw new BusinessException("Impossible d'annuler une transaction déjà traitée: " + transactionId);
        }

        transaction.setStatus(TransactionStatus.FAILED);
        transaction.setCompletedAt(LocalDateTime.now());

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toResponseDto(saved);
    }

    @Override
    public void assignToSyncBatch(UUID transactionId, UUID syncBatchId) {
        logger.info("Association de la transaction {} au lot de sync {}", transactionId, syncBatchId);
        transactionRepository.updateSyncBatchId(transactionId, syncBatchId);
    }

    @Override
    public BigDecimal calculateOverpayment(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée"));

        if (transaction.getTransferredAmount() == null || transaction.getRequestedAmount() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal diff = transaction.getTransferredAmount().subtract(transaction.getRequestedAmount());
        return diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO;
    }

    // ============ Méthodes privées ============

    private void validateWalletsExist(UUID senderId, UUID receiverId) {
        if (!walletService.existsById(senderId)) {
            throw new BusinessException("Wallet expéditeur introuvable: " + senderId);
        }
        if (!walletService.existsById(receiverId)) {
            throw new BusinessException("Wallet destinataire introuvable: " + receiverId);
        }
    }

    private void validateSufficientFunds(UUID walletId, BigDecimal amount) {
        BigDecimal balance = walletService.getOnlineBalance(walletId);
        if (balance.compareTo(amount) < 0) {
            throw new BusinessException("Fonds insuffisants. Solde: " + balance + ", Requis: " + amount);
        }
    }

    private String computeTransactionHash(Transaction transaction) {
        String data = transaction.getSenderWalletId().toString() +
                transaction.getReceiverWalletId().toString() +
                transaction.getRequestedAmount().toString() +
                transaction.getInitiatedAt().toString();
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            logger.error("Erreur lors du calcul du hash de transaction", e);
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}