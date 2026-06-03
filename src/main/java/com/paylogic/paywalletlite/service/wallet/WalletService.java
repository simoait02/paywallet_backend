package com.paylogic.paywalletlite.service.wallet;

import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.enums.CurrencyCode;
import com.paylogic.paywalletlite.dto.request.CreateWalletRequestDto;
import com.paylogic.paywalletlite.dto.request.WalletConfigUpdateRequestDto;
import com.paylogic.paywalletlite.dto.response.WalletResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface WalletService {

    WalletResponseDto requestWalletCreation(UUID userId, CreateWalletRequestDto request);

    WalletResponseDto getWalletById(UUID walletId);

    Wallet findById(UUID walletId) throws BusinessException;


    List<WalletResponseDto> getWalletsByUserId(UUID userId);

    List<WalletResponseDto> getPendingWallets();


    WalletResponseDto rejectWallet(UUID walletId, String reason);


    // ============================================================
    // NOUVELLES MÉTHODES — WORKFLOW APPROBATION
    // ============================================================

    /**
     * Approuve un wallet en attente.
     * Status: PENDING_APPROVAL → APPROVED
     */
    WalletResponseDto approveWallet(UUID walletId) throws BusinessException;


    /**
     * Configure un wallet approuvé (limites, règles).
     * Crée ou met à jour WalletConfig.
     */
    WalletResponseDto configureWallet(UUID walletId, WalletConfigUpdateRequestDto configDto) throws BusinessException;

    /**
     * Active un wallet configuré.
     * Status: APPROVED → ACTIVE
     */
    WalletResponseDto activateWallet(UUID walletId) throws BusinessException;

    // ============================================================
    // NOUVELLES MÉTHODES — GESTION DES STATUTS
    // ============================================================

    /**
     * Verrouille un wallet actif.
     * Status: ACTIVE → LOCKED
     */
    WalletResponseDto lockWallet(UUID walletId, String reason) throws BusinessException;

    /**
     * Déverrouille un wallet verrouillé.
     * Status: LOCKED → ACTIVE
     */
    WalletResponseDto unlockWallet(UUID walletId) throws BusinessException;

    /**
     * Gèle un wallet (investigation, litige).
     * Status: ACTIVE → FROZEN
     */
    WalletResponseDto freezeWallet(UUID walletId, String reason) throws BusinessException;

    /**
     * Clôture définitivement un wallet.
     * Status: * → CLOSED
     */
    WalletResponseDto closeWallet(UUID walletId, String reason) throws BusinessException;

    /**
     * Retourne le wallet ACTIVE de l'utilisateur.
     */
    WalletResponseDto getActiveWalletByUserId(UUID userId) throws BusinessException;

    /**
     * Crédite le wallet d'un montant spécifié.
     *
     * Vérifications :
     * - Le wallet doit être ACTIVE
     * - Le montant doit être strictement positif
     * - Le montant ne doit pas dépasser la limite maximale autorisée
     *
     * @param walletId ID du wallet à créditer
     * @param amount   Montant à créditer
     * @param fundingSource Source du financement
     * @param externalReference Référence externe optionnelle
     * @param notes Notes optionnelles
     * @return WalletResponseDto mis à jour
     * @throws BusinessException si le wallet n'est pas trouvé ou n'est pas ACTIVE
     */
    WalletResponseDto fundWallet(UUID walletId, BigDecimal amount, CurrencyCode currency, String fundingSource,
                                 String externalReference, String notes) throws BusinessException;

    // ============ NOUVELLES MÉTHODES — BLOC 7.4 ============

    /**
     * Vérifie l'existence d'un wallet par son ID.
     */
    boolean existsById(UUID walletId);

    /**
     * Récupère le solde online d'un wallet.
     */
    BigDecimal getOnlineBalance(UUID walletId);

    /**
     * Crédite le solde online d'un wallet.
     */
    void creditBalance(UUID walletId, BigDecimal amount);

    /**
     * Débite le solde online d'un wallet.
     */
    void debitBalance(UUID walletId, BigDecimal amount);


    /**
     * Crédite le solde en attente online d'un wallet.
     */
    void creditPendingBalance(UUID walletId, BigDecimal amount);

    /**
     * Débite le solde en attente online d'un wallet.
     */
    void debitPendingBalance(UUID walletId, BigDecimal amount);

    /**
     * Enregistre une dette de crédit sur un wallet.
     */
    void recordCreditDebt(UUID walletId, BigDecimal amount);
}