package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service orchestrateur qui sélectionne et applique la stratégie de génération appropriée.
 */
@Service
public class TokenGenerationService {

    private final List<TokenGenerationStrategy> strategies;
    private final SlidingWindowTokenGenerator defaultGenerator;

    @Autowired
    public TokenGenerationService(List<TokenGenerationStrategy> strategies,
                                  SlidingWindowTokenGenerator defaultGenerator) {
        this.strategies = strategies;
        this.defaultGenerator = defaultGenerator;
    }

    /**
     * Génère des tokens en utilisant la meilleure stratégie disponible pour le wallet.
     */
    public List<Token> generate(Wallet wallet, BigDecimal amount) {
        //TokenGenerationStrategy strategy = selectStrategy(wallet);
        return defaultGenerator.generateTokens(wallet, amount);
    }

    /**
     * Sélectionne la stratégie appropriée pour le wallet.
     */
    private TokenGenerationStrategy selectStrategy(Wallet wallet) {
        for (TokenGenerationStrategy strategy : strategies) {
            if (strategy.supports(wallet)) {
                return strategy;
            }
        }
        // Fallback sur le générateur par défaut
        //if (defaultGenerator.supports(wallet)) {
            return defaultGenerator;
        //}
        /*
        throw new BusinessException("No token generation strategy available for wallet: "
                + wallet.getWalletId());

         **/
    }
}