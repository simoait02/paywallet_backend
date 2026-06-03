package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
import com.paylogic.paywalletlite.domain.token.enums.AllocationMode;
import com.paylogic.paywalletlite.domain.token.enums.OfflineTokenValidationStatus;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.transaction.SyncBatch;
import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.enums.*;
import com.paylogic.paywalletlite.dto.request.OfflineTokenDto;
import com.paylogic.paywalletlite.dto.request.OfflineTransactionDto;
import com.paylogic.paywalletlite.dto.request.SyncRequestDto;
import com.paylogic.paywalletlite.dto.request.TransferNodeDto;
import com.paylogic.paywalletlite.dto.response.SyncResponseDto;
import com.paylogic.paywalletlite.dto.response.TokenRedemptionResultDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.exception.DoubleSpendException;
import com.paylogic.paywalletlite.exception.TokenExpiredException;
import com.paylogic.paywalletlite.repository.token.OfflineTransactionTokenRepository;
import com.paylogic.paywalletlite.repository.token.TokenRepository;
import com.paylogic.paywalletlite.repository.token.TokenTransferNodeRepository;
import com.paylogic.paywalletlite.repository.transaction.SyncBatchRepository;
import com.paylogic.paywalletlite.repository.transaction.TransactionRepository;
import com.paylogic.paywalletlite.service.audit.AuditService;
import com.paylogic.paywalletlite.service.security.SignatureVerificationService;
import com.paylogic.paywalletlite.service.token.TokenSignatureService;
import com.paylogic.paywalletlite.service.wallet.WalletService;
import com.paylogic.paywalletlite.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service de réconciliation — Refondu pour le workflow batch.
 *
 * Flux :
 * 1. Recevoir le SyncRequestDto contenant les OfflineTransactionDto
 * 2. Persister le SyncBatch + Transactions + Tokens + TransferNodes
 * 3. Traiter le batch (validation, redemption, ledger, overpayment)
 * 4. Retourner le résultat
 */
@Service
@Transactional
public class ReconciliationServiceImpl implements ReconciliationService {

    private static final Logger logger = LoggerFactory.getLogger(ReconciliationServiceImpl.class);

    private final SyncBatchRepository syncBatchRepository;
    private final TransactionRepository transactionRepository;
    private final TokenRepository tokenRepository;
    private final TokenTransferNodeRepository transferNodeRepository;
    private final WalletService walletService;
    private final LedgerService ledgerService;
    private final OverpaymentHandler overpaymentHandler;
    private final SignatureVerificationService signatureVerificationService;
    private final TokenSignatureService tokenSignatureService;
    private final OfflineTransactionTokenRepository offlineTransactionTokenRepository;
    private final AuditService auditService;

    @Autowired
    public ReconciliationServiceImpl(SyncBatchRepository syncBatchRepository,
                                     TransactionRepository transactionRepository,
                                     TokenRepository tokenRepository,
                                     TokenTransferNodeRepository transferNodeRepository,
                                     WalletService walletService,
                                     LedgerService ledgerService,
                                     OverpaymentHandler overpaymentHandler,
                                     SignatureVerificationService signatureVerificationService,
                                     TokenSignatureService tokenSignatureService,
                                     AuditService auditService,
                                     OfflineTransactionTokenRepository offlineTransactionTokenRepository) {
        this.syncBatchRepository = syncBatchRepository;
        this.transactionRepository = transactionRepository;
        this.tokenRepository = tokenRepository;
        this.transferNodeRepository = transferNodeRepository;
        this.walletService = walletService;
        this.ledgerService = ledgerService;
        this.overpaymentHandler = overpaymentHandler;
        this.signatureVerificationService = signatureVerificationService;
        this.tokenSignatureService = tokenSignatureService;
        this.auditService = auditService;
        this.offlineTransactionTokenRepository = offlineTransactionTokenRepository;
    }

    // ============================================================
    // PHASE 1 : RÉCEPTION ET PERSISTANCE DU BATCH
    // ============================================================

    @Override
    public SyncResponseDto synchronize(SyncRequestDto request) {
        logger.info("=== DÉBUT SYNCHRONISATION ===");
        logger.info("Wallet: {}, Device: {}, Transactions: {}",
                request.getWalletId(), request.getDeviceId(), request.getTransactions().size());

        // Étape 1 : Créer le SyncBatch
        SyncBatch batch = createSyncBatch(request);
        logger.info("SyncBatch créé: {}", batch.getBatchId());
        // Début de sync
        auditService.logSync(batch.getBatchId(), request.getWalletId(),
                AuditEventType.SYNC_INITIATED, 0, 0, "Démarrage synchronisation");

        // Étape 2 : Persister toutes les transactions du batch
        List<Transaction> persistedTransactions = persistTransactions(request, batch);

        // Étape 3 : Traiter le batch
        BatchResult result = processBatch(batch, persistedTransactions);

        // Étape 4 : Finaliser le batch
        finalizeBatch(batch, result);



        // Étape 5 : Construire la réponse
        return buildSyncResponse(batch, result);
    }

    // ============================================================
    // ÉTAPE 1 : CRÉATION DU SYNCBATCH
    // ============================================================

    private SyncBatch createSyncBatch(SyncRequestDto request) {
        SyncBatch batch = new SyncBatch();
        batch.setWalletId(request.getWalletId());
        batch.setDeviceId(request.getDeviceId());
        batch.setStatus(SyncBatchStatus.INITIATED);
        batch.setStartedAt(LocalDateTime.now());
        batch.setTransactionCount(request.getTransactions().size());

        // Capturer le solde avant traitement
        BigDecimal balanceBefore = walletService.getOnlineBalance(request.getWalletId());
        batch.setExpectedBalanceBefore(balanceBefore);

        return syncBatchRepository.save(batch);
    }

    // ============================================================
    // ÉTAPE 2 : PERSISTANCE DES TRANSACTIONS, TOKENS, TRANSFERT NODES
    // ============================================================

    /**
     * ÉTAPE 2 : PERSISTANCE DES TRANSACTIONS ET TOKENS OFFLINE
     *
     * Ne recrée PAS les tokens existants.
     * Utilise OfflineTransactionToken pour :
     * - Stocker les données reçues du device
     * - Référencer les tokens originaux
     * - Permettre la validation ultérieure
     */
    private List<Transaction> persistTransactions(SyncRequestDto request, SyncBatch batch) {
        List<Transaction> persisted = new ArrayList<>();

        for (OfflineTransactionDto txDto : request.getTransactions()) {

            // 1. Créer la Transaction
            Transaction transaction = new Transaction();
            transaction.setSenderWalletId(txDto.getSenderWalletId());
            transaction.setReceiverWalletId(txDto.getReceiverWalletId());
            transaction.setRequestedAmount(txDto.getRequestedAmount());
            transaction.setTransferredAmount(BigDecimal.ZERO); // Sera calculé
            transaction.setType(txDto.getType());
            transaction.setStatus(TransactionStatus.OFFLINE_PENDING);
            transaction.setIsOffline(true);
            transaction.setOfflineSignature(txDto.getPayerSignature());
            transaction.setInitiatedAt(DateTimeUtil.parseDateTime(txDto.getTransferTimestamp()));
            transaction.setSyncBatchId(batch.getBatchId());

            transaction = transactionRepository.save(transaction);

            // 2. Persister les OfflineTransactionToken (sans recréer les tokens)
            BigDecimal totalTransferred = BigDecimal.ZERO;
            List<String> tokenIdList = new ArrayList<>();

            for (OfflineTokenDto tokenDto : txDto.getTokens()) {

                // Vérifier si le token original existe
                boolean tokenExists = tokenRepository.existsById(tokenDto.getTokenId());

                if (tokenExists) {
                    // Vérifier qu'il n'est pas déjà REDEEMED
                    Token existingToken = tokenRepository.findById(tokenDto.getTokenId()).get();
                    if (existingToken.getStatus() == TokenStatus.REDEEMED) {
                        throw new DoubleSpendException(
                                "Token already redeemed: " + tokenDto.getTokenId());
                    }
                }

                // Vérifier que ce token n'a pas déjà été soumis dans ce batch
                boolean alreadySubmitted = offlineTransactionTokenRepository
                        .existsByTokenIdAndValidationStatus(
                                tokenDto.getTokenId(),
                                OfflineTokenValidationStatus.REDEEMED);
                if (alreadySubmitted) {
                    throw new DoubleSpendException(
                            "Token already submitted for redemption: " + tokenDto.getTokenId());
                }

                // Créer l'OfflineTransactionToken (stockage temporaire pour validation)
                OfflineTransactionToken offlineToken = new OfflineTransactionToken();
                offlineToken.setTransactionId(transaction.getTransactionId());
                offlineToken.setTokenId(tokenDto.getTokenId());
                offlineToken.setIssuerId(tokenDto.getIssuerId());
                offlineToken.setTokenValue(tokenDto.getValue());
                offlineToken.setTokenNonce(tokenDto.getNonce());
                offlineToken.setTokenHash(tokenDto.getTokenHash());
                offlineToken.setBackendSignature(tokenDto.getBackendSignature());
                offlineToken.setIssuedAt(DateTimeUtil.parseDateTime(tokenDto.getIssuedAt()));
                offlineToken.setExpiresAt(DateTimeUtil.parseDateTime(tokenDto.getExpiresAt()));
                offlineToken.setAllocationMode(tokenDto.getAllocationMode());
                offlineToken.setOriginalWalletId(tokenDto.getOriginalWalletId());
                offlineToken.setValidationStatus(tokenDto.getValidationStatus());

                // Informations du dernier transfert
                if (!tokenDto.getTransferNodes().isEmpty()) {
                    System.out.println("Token Transfer Nodes : "+ tokenDto.getTransferNodes().size());
                    TransferNodeDto lastNode = tokenDto.getTransferNodes()
                            .get(tokenDto.getTransferNodes().size() - 1);
                    offlineToken.setPayerWalletId(lastNode.getPayerWalletId());
                    offlineToken.setPayeeWalletId(lastNode.getPayeeWalletId());
                    offlineToken.setTransferredAmount(lastNode.getTransferredAmount());
                    offlineToken.setTransferTimestamp(
                            DateTimeUtil.parseDateTime(lastNode.getTransferTimestamp()));
                    offlineToken.setPayerSignature(lastNode.getPayerSignature());
                    offlineToken.setPayerCertificate(lastNode.getPayerCertificate());
                    offlineToken.setTransferHash(lastNode.getTransferHash());
                    offlineToken.setSequenceNumber(lastNode.getSequenceNumber());
                }

                // Stocker la chaîne complète en JSON pour validation ultérieure
                try {
                    com.fasterxml.jackson.databind.ObjectMapper mapper =
                            new com.fasterxml.jackson.databind.ObjectMapper();
                    offlineToken.setTransferChainJson(
                            mapper.writeValueAsString(tokenDto.getTransferNodes()));
                } catch (Exception e) {
                    throw new BusinessException("Failed to serialize transfer chain: " + e.getMessage());
                }

                offlineToken.setValidationStatus(OfflineTokenValidationStatus.PENDING_VALIDATION);
                offlineTransactionTokenRepository.save(offlineToken);

                totalTransferred = totalTransferred.add(tokenDto.getValue());
                tokenIdList.add(tokenDto.getTokenId().toString());
            }

            // 3. Mettre à jour les montants de la transaction
            transaction.setTransferredAmount(totalTransferred);

            // Calculer l'overpayment
            BigDecimal overpayment = totalTransferred.subtract(txDto.getRequestedAmount());
            if (overpayment.compareTo(BigDecimal.ZERO) > 0) {
                transaction.setOverpaymentAmount(overpayment);
                transaction.setOverpaymentStatus(OverpaymentStatus.PENDING_REFUND);
            }

            // Stocker la liste des IDs de tokens (pour référence rapide)
            transaction.setTransactionHash(String.join(",", tokenIdList));
            transaction = transactionRepository.save(transaction);
            persisted.add(transaction);

            logger.info("Transaction persistée: {} avec {} tokens offline",
                    transaction.getTransactionId(), tokenIdList.size());
        }

        return persisted;
    }

    /**
     * ÉTAPE 3 : TRAITEMENT DU BATCH
     *
     * Pour chaque transaction :
     * 1. Récupère les OfflineTransactionToken
     * 2. Valide chaque token (signature backend, chaîne, non-expiration)
     * 3. Compare avec le token original
     * 4. Effectue la redemption
     * 5. Marque comme REDEEMED
     */
    private BatchResult processBatch(SyncBatch batch, List<Transaction> transactions) {
        batch.setStatus(SyncBatchStatus.PROCESSING);
        syncBatchRepository.save(batch);

        BatchResult result = new BatchResult();
        List<TokenRedemptionResultDto> redemptionResults = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;
        BigDecimal totalSynced = BigDecimal.ZERO;
        BigDecimal totalFailed = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            try {
                // Récupérer les OfflineTransactionToken de cette transaction
                List<OfflineTransactionToken> offlineTokens = offlineTransactionTokenRepository
                        .findByTransactionIdAndValidationStatus(
                                transaction.getTransactionId(),
                                OfflineTokenValidationStatus.PENDING_VALIDATION);

                if (offlineTokens.isEmpty()) {
                    throw new BusinessException("No pending tokens for transaction: "
                            + transaction.getTransactionId());
                }

                BigDecimal totalCredited = BigDecimal.ZERO;
                boolean allValid = true;

                for (OfflineTransactionToken offlineToken : offlineTokens) {

                    try {
                        // Étape 3a : Valider le token offline
                        validateOfflineToken(offlineToken, transaction.getReceiverWalletId());

                        // Étape 3b : Si le token original existe, le mettre à jour
                        if (tokenRepository.existsById(offlineToken.getTokenId())) {
                            Token originalToken = tokenRepository.findById(offlineToken.getTokenId()).get();

                            // Mettre à jour le statut
                            originalToken.setStatus(TokenStatus.REDEEMED);
                            originalToken.setCurrentHolderWallet(walletService.findById(transaction.getReceiverWalletId()));
                            tokenRepository.save(originalToken);
                        } else {
                            // Le token n'existe pas encore (cas rare : allocation conditionnelle)
                            // On le crée maintenant pour la traçabilité
                            Token newToken = new Token();
                            newToken.setTokenId(offlineToken.getTokenId());
                            newToken.setValue(offlineToken.getTokenValue());
                            newToken.setNonce(offlineToken.getTokenNonce());
                            newToken.setTokenHash(offlineToken.getTokenHash());
                            newToken.setStatus(TokenStatus.REDEEMED);
                            newToken.setAllocationMode(AllocationMode.valueOf(offlineToken.getAllocationMode()));
                            newToken.setOriginalWallet(walletService.findById(offlineToken.getOriginalWalletId()));
                            newToken.setCurrentHolderWallet(walletService.findById(transaction.getReceiverWalletId()));
                            newToken.setExpiresAt(offlineToken.getExpiresAt());
                            tokenRepository.save(newToken);
                        }

                        // Marquer comme validé
                        offlineToken.setValidationStatus(OfflineTokenValidationStatus.REDEEMED);
                        offlineTransactionTokenRepository.save(offlineToken);

                        totalCredited = totalCredited.add(offlineToken.getTokenValue());

                    } catch (Exception e) {
                        allValid = false;
                        offlineToken.setValidationStatus(
                                determineFailureStatus(e));
                        offlineTransactionTokenRepository.save(offlineToken);
                        logger.error("Échec validation token {}: {}",
                                offlineToken.getTokenId(), e.getMessage());
                    }
                }

                if (allValid) {
                    // Créditer le receiver du montant total
                    walletService.creditPendingBalance(
                            transaction.getReceiverWalletId(), totalCredited);
                    walletService.creditBalance(
                            transaction.getReceiverWalletId(), totalCredited);
                    walletService.debitPendingBalance(
                            transaction.getReceiverWalletId(), totalCredited);

                    // ============================================================
                    // GESTION DU SURPAIEMENT (appel unique)
                    // ============================================================
                    // detectOverpayment() gère TOUT :
                    // - Calcul du surpaiement
                    // - Création du TransactionRefund
                    // - Remboursement automatique (débit receiver, crédit sender)
                    // - Mise à jour des statuts
                    overpaymentHandler.detectOverpayment(transaction, totalCredited);

                    // Settlement (débit sender selon AllocationMode)
                    for (OfflineTransactionToken ot : offlineTokens) {
                        executeSettlement(null, ot.getTokenId());
                    }

                    // Ledger (après remboursement pour avoir les montants finaux)
                    ledgerService.recordDoubleEntry(
                            transaction.getTransactionId(),
                            transaction.getSenderWalletId(),
                            transaction.getReceiverWalletId(),
                            // Le montant net après remboursement
                            totalCredited.subtract(
                                    transaction.getOverpaymentAmount() != null
                                            ? transaction.getOverpaymentAmount()
                                            : BigDecimal.ZERO),
                            "Offline settlement — batch: " + batch.getBatchId());

                    // Finaliser
                    transaction.setStatus(TransactionStatus.COMPLETED);
                    transaction.setCompletedAt(LocalDateTime.now());
                    transactionRepository.save(transaction);

                    successCount++;
                    totalSynced = totalSynced.add(totalCredited);

                    TokenRedemptionResultDto r = new TokenRedemptionResultDto();
                    r.setRedeemed(true);
                    r.setCreditedAmount(totalCredited);
                    r.setFinalStatus(TransactionStatus.COMPLETED);
                    redemptionResults.add(r);

                } else {
                    transaction.setStatus(TransactionStatus.FAILED);
                    transactionRepository.save(transaction);
                    failureCount++;
                    totalFailed = totalFailed.add(totalCredited);

                    TokenRedemptionResultDto r = new TokenRedemptionResultDto();
                    r.setRedeemed(false);
                    r.setFailureReason("Token validation failed");
                    redemptionResults.add(r);
                }

            } catch (Exception e) {
                logger.error("Échec traitement transaction {}: {}",
                        transaction.getTransactionId(), e.getMessage());
                transaction.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(transaction);
                failureCount++;
                totalFailed = totalFailed.add(transaction.getTransferredAmount());
            }
        }

        // Fin de sync
        auditService.logSync(batch.getBatchId(), batch.getWalletId(),
                batch.getStatus() == SyncBatchStatus.COMPLETED
                        ? AuditEventType.SYNC_COMPLETED
                        : AuditEventType.SYNC_PARTIAL,
                successCount, failureCount,
                "Sync terminée : " + successCount + "/" + (successCount + failureCount));

        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setTotalSynced(totalSynced);
        result.setTotalFailed(totalFailed);
        result.setRedemptionResults(redemptionResults);

        return result;
    }

// ============================================================
// VALIDATION D'UN TOKEN OFFLINE
// ============================================================

    private void validateOfflineToken(OfflineTransactionToken offlineToken, UUID expectedReceiverId) {
        // 1. Vérifier l'expiration
        if (offlineToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token expired: " + offlineToken.getTokenId());
        }

        // 2. Vérifier que le dernier détenteur est bien le receiver
        if (!offlineToken.getPayeeWalletId().equals(expectedReceiverId)) {
            throw new BusinessException(String.format(
                    "Holder mismatch: expected %s but last holder is %s",
                    expectedReceiverId, offlineToken.getPayeeWalletId()));
        }

        // 3. Vérifier la signature du backend sur le token
        boolean backendSigValid = tokenSignatureService.verifyTokenSignature(offlineToken);
        if (!backendSigValid) {
            throw new BusinessException("Backend signature invalid for token: "
                    + offlineToken.getTokenId());
        }

        // 4. Si le token original existe, comparer les données
        if (tokenRepository.existsById(offlineToken.getTokenId())) {
            Token original = tokenRepository.findById(offlineToken.getTokenId()).get();

            if (!original.getNonce().equals(offlineToken.getTokenNonce())) {
                throw new BusinessException("Nonce mismatch for token: "
                        + offlineToken.getTokenId());
            }

            if (original.getValue().compareTo(offlineToken.getTokenValue()) != 0) {
                throw new BusinessException("Value mismatch for token: "
                        + offlineToken.getTokenId());
            }

            if (original.getStatus() == TokenStatus.REDEEMED) {
                throw new DoubleSpendException("Token already redeemed: "
                        + offlineToken.getTokenId());
            }
        }

        // 5. Vérifier la chaîne de transfert (depuis le JSON stocké)
        if (offlineToken.getTransferChainJson() != null) {
            validateTransferChainFromJson(offlineToken);
        }
    }

    private void validateTransferChainFromJson(OfflineTransactionToken offlineToken) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            List<TransferNodeDto> nodes = mapper.readValue(
                    offlineToken.getTransferChainJson(),
                    mapper.getTypeFactory().constructCollectionType(List.class, TransferNodeDto.class));

            // Vérifier la continuité de la chaîne
            for (int i = 1; i < nodes.size(); i++) {
                TransferNodeDto prev = nodes.get(i - 1);
                TransferNodeDto curr = nodes.get(i);
                if (!prev.getPayeeWalletId().equals(curr.getPayerWalletId())) {
                    throw new BusinessException("Transfer chain broken at sequence " + i);
                }
            }

        } catch (Exception e) {
            throw new BusinessException("Failed to validate transfer chain: " + e.getMessage());
        }
    }

    private OfflineTokenValidationStatus determineFailureStatus(Exception e) {
        if (e instanceof DoubleSpendException) return OfflineTokenValidationStatus.ALREADY_REDEEMED;
        if (e instanceof TokenExpiredException) return OfflineTokenValidationStatus.EXPIRED;
        if (e.getMessage().contains("signature")) return OfflineTokenValidationStatus.INVALID_BACKEND_SIGNATURE;
        if (e.getMessage().contains("chain")) return OfflineTokenValidationStatus.INVALID_TRANSFER_CHAIN;
        if (e.getMessage().contains("Holder mismatch")) return OfflineTokenValidationStatus.HOLDER_MISMATCH;
        return OfflineTokenValidationStatus.PENDING_VALIDATION;
    }
    // ============================================================
    // ÉTAPE 4 : FINALISATION DU BATCH
    // ============================================================

    private void finalizeBatch(SyncBatch batch, BatchResult result) {
        batch.setCompletedAt(LocalDateTime.now());
        batch.setTotalAmount(result.getTotalSynced().add(result.getTotalFailed()));

        // Calculer le solde attendu après traitement
        BigDecimal expectedAfter = batch.getExpectedBalanceBefore().add(result.getTotalSynced());
        batch.setExpectedBalanceAfter(expectedAfter);

        // Récupérer le solde réel
        BigDecimal actualAfter = walletService.getOnlineBalance(batch.getWalletId());
        batch.setActualBalanceAfter(actualAfter);

        // Calculer l'écart
        BigDecimal discrepancy = actualAfter.subtract(expectedAfter);
        batch.setDiscrepancy(discrepancy);

        // Déterminer le statut final
        if (result.getFailureCount() == 0 && discrepancy.compareTo(BigDecimal.ZERO) == 0) {
            batch.setStatus(SyncBatchStatus.COMPLETED);
        } else if (result.getSuccessCount() > 0 && result.getFailureCount() > 0) {
            batch.setStatus(SyncBatchStatus.PARTIAL);
        } else {
            batch.setStatus(SyncBatchStatus.FAILED);
        }

        syncBatchRepository.save(batch);

        logger.info("=== BATCH FINALISÉ ===");
        logger.info("Batch: {}, Status: {}, Succès: {}, Échecs: {}, Discrepancy: {}",
                batch.getBatchId(), batch.getStatus(),
                result.getSuccessCount(), result.getFailureCount(), discrepancy);
    }

    // ============================================================
    // ÉTAPE 5 : CONSTRUCTION RÉPONSE
    // ============================================================

    private SyncResponseDto buildSyncResponse(SyncBatch batch, BatchResult result) {
        SyncResponseDto response = new SyncResponseDto();
        response.setBatchId(batch.getBatchId());
        response.setWalletId(batch.getWalletId());
        response.setStatus(batch.getStatus());
        response.setTransactionCount(result.getSuccessCount() + result.getFailureCount());
        response.setSuccessCount(result.getSuccessCount());
        response.setFailureCount(result.getFailureCount());
        response.setTotalAmountSynced(result.getTotalSynced());
        response.setTotalAmountFailed(result.getTotalFailed());
        response.setStartedAt(batch.getStartedAt());
        response.setCompletedAt(batch.getCompletedAt());
        response.setRedemptionResults(result.getRedemptionResults());
        response.setDiscrepancyReport(generateDiscrepancyReport(batch.getBatchId()));
        return response;
    }

    // ============================================================
    // MÉTHODES UTILITAIRES
    // ============================================================

    private TokenTransferNode getLastTransferNode(OfflineTokenDto tokenDto) {
        List<TransferNodeDto> nodes = tokenDto.getTransferNodes();
        TransferNodeDto last = nodes.get(nodes.size() - 1);
        TokenTransferNode node = new TokenTransferNode();
        node.setPayeeWallet(null);
        return node;
    }

    private boolean verifyChainIntegrity(Token token) {
        List<TokenTransferNode> chain = transferNodeRepository
                .findByTokenIdOrdered(token.getTokenId());
        if (chain.isEmpty()) return false;

        for (int i = 0; i < chain.size(); i++) {
            if (i > 0) {
                TokenTransferNode prev = chain.get(i - 1);
                TokenTransferNode curr = chain.get(i);
                if (!prev.getPayeeWalletId().equals(curr.getPayerWalletId())) {
                    return false;
                }
            }
        }
        return true;
    }





    @Override
    public void executeSettlement(UUID transactionId, UUID tokenId) {
        Token token = tokenRepository.findById(tokenId)
                .orElseThrow(() -> new BusinessException("Token not found: " + tokenId));

        AllocationMode mode = token.getAllocationMode();
        if (mode == AllocationMode.CONDITIONAL_RESERVATION) {
            walletService.debitBalance(token.getOriginalWalletId(), token.getValue());
        } else if (mode == AllocationMode.CREDIT_BASED) {
            walletService.recordCreditDebt(token.getOriginalWalletId(), token.getValue());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReconciliationStatus getReconciliationStatus(UUID batchId) {
        SyncBatch batch = syncBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException("Lot introuvable: " + batchId));

        switch (batch.getStatus()) {
            case INITIATED:
            case VALIDATING:
            case PROCESSING:
                return ReconciliationStatus.PENDING;
            case COMPLETED:
                return ReconciliationStatus.COMPLETED;
            case FAILED:
                return ReconciliationStatus.FAILED;
            case PARTIAL:
                return ReconciliationStatus.COMPLETED;
            default:
                return ReconciliationStatus.PENDING;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String generateDiscrepancyReport(UUID batchId) {
        SyncBatch batch = syncBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException("Lot introuvable: " + batchId));

        StringBuilder report = new StringBuilder();
        report.append("╔══════════════════════════════════════════════════════════════╗\n");
        report.append("║           RAPPORT DE DISCREPANCY — PAYWALLET LITE            ║\n");
        report.append("╚══════════════════════════════════════════════════════════════╝\n");
        report.append("Batch ID:        ").append(batchId).append("\n");
        report.append("Wallet ID:       ").append(batch.getWalletId()).append("\n");
        report.append("Status:          ").append(batch.getStatus()).append("\n");
        report.append("Transactions:    ").append(batch.getTransactionCount()).append("\n");
        report.append("Montant total:   ").append(batch.getTotalAmount()).append("\n");
        report.append("Solde attendu:   ").append(batch.getExpectedBalanceAfter()).append("\n");
        report.append("Solde réel:      ").append(batch.getActualBalanceAfter()).append("\n");
        report.append("Discrepancy:     ").append(batch.getDiscrepancy()).append("\n");
        if (batch.getDiscrepancy() != null && batch.getDiscrepancy().compareTo(BigDecimal.ZERO) != 0) {
            report.append("⚠️ ALERTE: Discrepancy détectée!\n");
        }
        report.append("Généré le:       ").append(LocalDateTime.now()).append("\n");

        return report.toString();
    }

    @Override
    public void cancelReconciliation(UUID batchId) {
        logger.info("Annulation reconciliation: {}", batchId);

        SyncBatch batch = syncBatchRepository.findById(batchId)
                .orElseThrow(() -> new BusinessException("Lot introuvable: " + batchId));

        if (batch.getStatus() == SyncBatchStatus.COMPLETED) {
            throw new BusinessException("Impossible d'annuler un lot complété");
        }

        batch.setStatus(SyncBatchStatus.FAILED);
        syncBatchRepository.save(batch);

        // Libérer les transactions
        List<Transaction> transactions = transactionRepository.findBySyncBatchId(batchId);
        for (Transaction tx : transactions) {
            tx.setSyncBatchId(null);
            tx.setStatus(TransactionStatus.OFFLINE_PENDING);
            transactionRepository.save(tx);
        }
    }

    // Classe interne pour le résultat du batch
    private static class BatchResult {
        private int successCount;
        private int failureCount;
        private BigDecimal totalSynced = BigDecimal.ZERO;
        private BigDecimal totalFailed = BigDecimal.ZERO;
        private List<TokenRedemptionResultDto> redemptionResults;

        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public BigDecimal getTotalSynced() { return totalSynced; }
        public void setTotalSynced(BigDecimal totalSynced) { this.totalSynced = totalSynced; }
        public BigDecimal getTotalFailed() { return totalFailed; }
        public void setTotalFailed(BigDecimal totalFailed) { this.totalFailed = totalFailed; }
        public List<TokenRedemptionResultDto> getRedemptionResults() { return redemptionResults; }
        public void setRedemptionResults(List<TokenRedemptionResultDto> r) { this.redemptionResults = r; }
    }
}