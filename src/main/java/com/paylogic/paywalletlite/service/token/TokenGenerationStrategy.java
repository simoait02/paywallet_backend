package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.wallet.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface TokenGenerationStrategy {

    /**
     * Génère une liste de tokens pour un montant donné selon la stratégie configurée.
     *
     * @param wallet Le wallet émetteur
     * @param totalAmount Le montant total à allouer
     * @return Liste des tokens générés (non persistés)
     */
    List<Token> generateTokens(Wallet wallet, BigDecimal totalAmount);

    /**
     * Vérifie si cette stratégie supporte le wallet donné.
     */
    boolean supports(Wallet wallet);

    /**
     * Nom unique de la stratégie.
     */
    String getStrategyName();
}