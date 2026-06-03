package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenTransferNode;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.exception.BusinessException;

import java.util.List;
import java.util.UUID;

public interface TokenTransferTraceService {

    /**
     * Crée le premier nœud : émission par le backend.
     * Backend → Alice
     */
    TokenTransferNode recordIssuance(Token token, Wallet issuerWallet) throws BusinessException;

    /**
     * Crée un nœud de transfert P2P offline.
     * Alice → Bob
     */
    TokenTransferNode recordTransfer(Token token, Wallet fromWallet, Wallet toWallet, String payerSignature) throws BusinessException;

    /**
     * Crée le nœud final : redemption.
     * Charlie → Backend (consommation)
     */
    TokenTransferNode recordRedemption(Token token, Wallet redeemerWallet, String validationSignature) throws BusinessException;

    /**
     * Reconstruit la chaîne complète de possession d'un token.
     * Backend → Alice → Bob → Charlie
     */
    List<TokenTransferNode> reconstructChain(UUID tokenId) throws BusinessException;

    /**
     * Vérifie l'intégrité cryptographique de la chaîne.
     * Chaque nœud doit être signé par le payer précédent.
     */
    boolean verifyChainIntegrity(UUID tokenId) throws BusinessException;

    /**
     * Retourne le dernier détenteur connu d'un token.
     */
    Wallet getCurrentHolder(UUID tokenId) throws BusinessException;

    /**
     * Retourne le premier émetteur d'un token.
     */
    Wallet getOriginalIssuer(UUID tokenId) throws BusinessException;

    /**
     * Compte le nombre de transferts effectués.
     */
    long getTransferCount(UUID tokenId);

    /**
     * Vérifie si un wallet a déjà détenu ce token.
     */
    boolean hasWalletHeldToken(UUID tokenId, UUID walletId);
}