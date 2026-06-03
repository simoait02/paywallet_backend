package com.paylogic.paywalletlite.service.transaction;

import com.paylogic.paywalletlite.domain.transaction.Transaction;
import com.paylogic.paywalletlite.domain.transaction.enums.OverpaymentStatus;
import com.paylogic.paywalletlite.dto.request.OverpaymentRefundRequestDto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Handler pour la gestion des surpaiements (overpayment).
 *
 * Un surpaiement se produit quand le payeur transfère des tokens
 * dont la valeur totale dépasse le montant dû.
 * Exemple: tokens de 100 + 50 pour un paiement de 120 → surpaiement de 30.
 *
 * Cycle de vie: PENDING_REFUND → REFUNDED | FORFEITED
 */
public interface OverpaymentHandler {

    /**
     * Détecte et enregistre un surpaiement lors d'une transaction.
     */
    void detectOverpayment(Transaction transaction, BigDecimal tokenTotalValue);

    /**
     * Traite le remboursement d'un surpaiement au payeur.
     */
    void processRefund(UUID transactionId, String processedBy);

    /**
     * Marque un surpaiement comme remboursé.

    void markAsRefunded(UUID transactionId);
     */
    /**
     * Marque un surpaiement comme abandonné (forfeited) après délai.
     */
    void markAsForfeited(UUID transactionId);

    /**
     * Récupère le statut de surpaiement d'une transaction.
     */
    OverpaymentStatus getOverpaymentStatus(UUID transactionId);

    /**
     * Calcule le montant du surpaiement.
     */
    BigDecimal calculateOverpaymentAmount(BigDecimal requestedAmount, BigDecimal transferredAmount);

    /**
     * Vérifie si une transaction présente un surpaiement.
     */
    boolean hasOverpayment(UUID transactionId);
}