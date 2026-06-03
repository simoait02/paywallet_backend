package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.TransactionRefund;
import com.paylogic.paywalletlite.domain.transaction.enums.OverpaymentStatus;
import com.paylogic.paywalletlite.domain.transaction.enums.RefundStatus;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.transaction.TransactionRefundRepository;
import com.paylogic.paywalletlite.repository.transaction.TransactionRepository;
import com.paylogic.paywalletlite.service.audit.AuditService;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implémentation du handler de surpaiement (Overpayment).
 *
 * Gère le cycle de vie complet d'un surpaiement :
 * 1. DÉTECTION : lors de la synchronisation, si transferred > requested
 * 2. REMBOURSEMENT AUTOMATIQUE : crédite le sender, débite le receiver
 * 3. FORFAITURE : après délai, si le remboursement n'a pas été traité
 *
 * Exemple : tokens de 100 + 50 + 20 = 170 pour un paiement de 150
 * → surpaiement de 20 → remboursé automatiquement au sender
 */
@Service
@Transactional
public class OverpaymentHandlerImpl implements OverpaymentHandler {

    private static final Logger logger = LoggerFactory.getLogger(OverpaymentHandlerImpl.class);

    @Value("${overpayment.refund.timeout.hours:72}")
    private int refundTimeoutHours;

    private final TransactionRepository transactionRepository;
    private final TransactionRefundRepository refundRepository;
    private final WalletService walletService;
    private final AuditService auditService;

    @Autowired
    public OverpaymentHandlerImpl(TransactionRepository transactionRepository,
                                  TransactionRefundRepository refundRepository,
                                  WalletService walletService,
                                  AuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.refundRepository = refundRepository;
        this.walletService = walletService;
        this.auditService = auditService;
    }

    // ============================================================
    // ÉTAPE 1 : DÉTECTION ET REMBOURSEMENT AUTOMATIQUE
    // ============================================================

    /**
     * Détecte un surpaiement et exécute le remboursement automatique.
     *
     * Appelé depuis ReconciliationServiceImpl.processBatch()
     * lorsque la transaction est valide et que transferred > requested.
     *
     * @param transaction      La transaction en cours de traitement
     * @param totalTransferred Montant total transféré (somme des tokens)
     */
    @Override
    public void detectOverpayment(Transaction transaction, BigDecimal totalTransferred) {
        if (transaction == null || totalTransferred == null) {
            logger.warn("detectOverpayment: paramètres null, ignoré");
            return;
        }

        BigDecimal requestedAmount = transaction.getRequestedAmount();
        BigDecimal overpaymentAmount = calculateOverpaymentAmount(requestedAmount, totalTransferred);

        // Pas de surpaiement → rien à faire
        if (overpaymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.debug("Aucun surpaiement: requested={}, transferred={}",
                    requestedAmount, totalTransferred);
            return;
        }

        // === AUDIT : Surpaiement détecté ===
        auditService.logOverpayment(
                transaction.getTransactionId(),
                transaction.getSenderWalletId(),
                overpaymentAmount,
                AuditEventType.OVERPAYMENT_DETECTED
        );
        logger.info("=== SURPAIEMENT DÉTECTÉ ===");
        logger.info("Transaction   : {}", transaction.getTransactionId());
        logger.info("Montant demandé  : {} MAD", requestedAmount);
        logger.info("Montant transféré: {} MAD", totalTransferred);
        logger.info("Surpaiement      : {} MAD", overpaymentAmount);
        logger.info("Sender (à rembourser) : {}", transaction.getSenderWalletId());
        logger.info("Receiver (débité)     : {}", transaction.getReceiverWalletId());

        // -------------------------------------------------------
        // ÉTAPE 1a : Mettre à jour la transaction
        // -------------------------------------------------------
        transaction.setOverpaymentAmount(overpaymentAmount);
        transaction.setOverpaymentStatus(OverpaymentStatus.PENDING_REFUND);
        transaction.setTransferredAmount(totalTransferred);
        transactionRepository.save(transaction);

        // -------------------------------------------------------
        // ÉTAPE 1b : Créer l'enregistrement de remboursement
        // -------------------------------------------------------
        TransactionRefund refund = new TransactionRefund();
        refund.setOriginalTransactionId(transaction.getTransactionId());
        refund.setWalletId(transaction.getSenderWalletId()); // Remboursé au payeur
        refund.setRefundAmount(overpaymentAmount);
        refund.setStatus(RefundStatus.PENDING);
        refund.setProcessedAt(LocalDateTime.now());
        refund.setProcessedBy("SYSTEM_AUTO");
        refundRepository.save(refund);

        logger.info("TransactionRefund créé: refundId={}", refund.getRefundId());

        // -------------------------------------------------------
        // ÉTAPE 1c : Exécuter le remboursement automatique
        // -------------------------------------------------------
        executeAutomaticRefund(transaction, refund);
    }

    /**
     * Exécute le remboursement automatique :
     * - Débite le receiver du montant du surpaiement
     * - Crédite le sender du même montant
     * - Marque la transaction comme REFUNDED
     * - Marque le refund comme PROCESSED
     */
    private void executeAutomaticRefund(Transaction transaction, TransactionRefund refund) {
        try {
            BigDecimal refundAmount = refund.getRefundAmount();

            logger.info("=== REMBOURSEMENT AUTOMATIQUE ===");
            logger.info("Débit du receiver  : {} de {} MAD",
                    transaction.getReceiverWalletId(), refundAmount);
            logger.info("Crédit du sender   : {} de {} MAD",
                    transaction.getSenderWalletId(), refundAmount);

            // 1. Débiter le receiver (il a reçu trop)
            walletService.debitBalance(
                    transaction.getReceiverWalletId(),
                    refundAmount
            );

            // 2. Créditer le sender (remboursement)
            walletService.creditBalance(
                    transaction.getSenderWalletId(),
                    refundAmount
            );

            // 3. Marquer la transaction comme remboursée
            transaction.setOverpaymentStatus(OverpaymentStatus.REFUNDED);
            transactionRepository.save(transaction);

            // 4. Marquer le refund comme traité
            refund.setStatus(RefundStatus.PROCESSED);
            refund.setProcessedAt(LocalDateTime.now());
            refund.setProcessedBy("SYSTEM_AUTO");
            refundRepository.save(refund);

            logger.info("=== REMBOURSEMENT EFFECTUÉ AVEC SUCCÈS ===");
            logger.info("Sender {} crédité de {} MAD",
                    transaction.getSenderWalletId(), refundAmount);
            logger.info("Receiver {} débité de {} MAD",
                    transaction.getReceiverWalletId(), refundAmount);

            // === AUDIT : Remboursement effectué ===
            auditService.logOverpayment(
                    transaction.getTransactionId(),
                    transaction.getSenderWalletId(),
                    refund.getRefundAmount(),
                    AuditEventType.OVERPAYMENT_REFUNDED
            );

        } catch (Exception e) {
            logger.error("Échec du remboursement automatique: {}", e.getMessage(), e);

            // Laisser en PENDING_REFUND pour traitement manuel ultérieur
            transaction.setOverpaymentStatus(OverpaymentStatus.PENDING_REFUND);
            transactionRepository.save(transaction);

            refund.setStatus(RefundStatus.FAILED);
            refund.setProcessedBy("SYSTEM_AUTO_FAILED");
            refundRepository.save(refund);

            logger.warn("Remboursement laissé en PENDING pour traitement manuel");
        }
    }

    // ============================================================
    // ÉTAPE 2 : REMBOURSEMENT MANUEL (si l'auto a échoué)
    // ============================================================

    /**
     * Traitement manuel d'un remboursement.
     * Utilisé si le remboursement automatique a échoué.
     */
    @Override
    public void processRefund(UUID transactionId, String processedBy) {
        logger.info("Traitement manuel du remboursement: transaction={}, par={}",
                transactionId, processedBy);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));

        if (transaction.getOverpaymentStatus() != OverpaymentStatus.PENDING_REFUND) {
            throw new BusinessException(
                    "Aucun remboursement en attente. Statut actuel: " + transaction.getOverpaymentStatus());
        }

        TransactionRefund refund = refundRepository
                .findPendingByTransactionId(transactionId)
                .orElseThrow(() -> new BusinessException("Enregistrement de remboursement introuvable"));

        try {
            // 1. Débiter le receiver
            walletService.debitBalance(
                    transaction.getReceiverWalletId(),
                    refund.getRefundAmount()
            );

            // 2. Créditer le sender
            walletService.creditBalance(
                    transaction.getSenderWalletId(),
                    refund.getRefundAmount()
            );

            // 3. Marquer comme remboursé
            transaction.setOverpaymentStatus(OverpaymentStatus.REFUNDED);
            transactionRepository.save(transaction);

            // 4. Marquer le refund comme traité
            refund.setStatus(RefundStatus.PROCESSED);
            refund.setProcessedAt(LocalDateTime.now());
            refund.setProcessedBy(processedBy != null ? processedBy : "ADMIN");
            refundRepository.save(refund);

            logger.info("Remboursement manuel effectué: sender={}, amount={}",
                    transaction.getSenderWalletId(), refund.getRefundAmount());

        } catch (Exception e) {
            logger.error("Échec du remboursement manuel: {}", e.getMessage(), e);
            throw new BusinessException("Échec du remboursement: " + e.getMessage());
        }
    }

    // ============================================================
    // ÉTAPE 3 : FORFAITURE (après timeout)
    // ============================================================

    /**
     * Marque un surpaiement comme abandonné (forfeited).
     * L'argent reste chez le receiver.
     */
    @Override
    public void markAsForfeited(UUID transactionId) {
        logger.info("Forfaiture du surpaiement: transaction={}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));

        if (transaction.getOverpaymentStatus() != OverpaymentStatus.PENDING_REFUND) {
            throw new BusinessException(
                    "Le surpaiement n'est pas en attente. Statut: " + transaction.getOverpaymentStatus());
        }

        transaction.setOverpaymentStatus(OverpaymentStatus.FORFEITED);
        transactionRepository.save(transaction);

        // Annuler le refund associé
        TransactionRefund refund = refundRepository
                .findPendingByTransactionId(transactionId)
                .orElse(null);

        if (refund != null) {
            refund.setStatus(RefundStatus.FAILED);
            refund.setProcessedAt(LocalDateTime.now());
            refund.setProcessedBy("SYSTEM_AUTO_FORFEIT");
            refundRepository.save(refund);
        }

        logger.info("Surpaiement abandonné: transaction={}, amount={}",
                transactionId, transaction.getOverpaymentAmount());
    }

    // ============================================================
    // REQUÊTES
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public OverpaymentStatus getOverpaymentStatus(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));

        return transaction.getOverpaymentStatus() != null
                ? transaction.getOverpaymentStatus()
                : OverpaymentStatus.PENDING_REFUND;
    }

    @Override
    public BigDecimal calculateOverpaymentAmount(BigDecimal requestedAmount, BigDecimal transferredAmount) {
        if (requestedAmount == null || transferredAmount == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal diff = transferredAmount.subtract(requestedAmount);
        return diff.compareTo(BigDecimal.ZERO) > 0 ? diff : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasOverpayment(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException("Transaction non trouvée: " + transactionId));

        return transaction.getOverpaymentAmount() != null
                && transaction.getOverpaymentAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    // ============================================================
    // JOB PLANIFIÉ : FORFAITURE AUTOMATIQUE
    // ============================================================

    /**
     * Job planifié pour forfaiture automatique des remboursements expirés.
     * Exécuté via Quartz ou @Scheduled.
     */
    @Transactional
    public void processExpiredRefunds() {
        logger.info("=== TRAITEMENT DES REMBOURSEMENTS EXPIRÉS ===");
        logger.info("Timeout configuré: {} heures", refundTimeoutHours);

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(refundTimeoutHours);
        int processedCount = 0;

        for (TransactionRefund refund : refundRepository.findByStatus(RefundStatus.PENDING)) {
            // Vérifier si le refund a dépassé le délai
            // On utilise la date de création du refund (pas de processedAt car PENDING)
            if (refund.getCreatedAt() != null && refund.getCreatedAt().isBefore(cutoffTime)) {

                logger.info("Forfaiture automatique: refund={}, transaction={}, age={}h",
                        refund.getRefundId(),
                        refund.getOriginalTransactionId(),
                        java.time.Duration.between(refund.getCreatedAt(), LocalDateTime.now()).toHours());

                try {
                    markAsForfeited(refund.getOriginalTransactionId());
                    processedCount++;
                } catch (Exception e) {
                    logger.error("Échec forfaiture refund {}: {}",
                            refund.getRefundId(), e.getMessage());
                }
            }
        }

        logger.info("=== FORFAITURE TERMINÉE : {} remboursements traités ===", processedCount);
    }
}