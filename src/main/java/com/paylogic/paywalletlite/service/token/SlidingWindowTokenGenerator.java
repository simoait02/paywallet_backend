package com.paylogic.paywalletlite.service.token;

import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.token.TokenDenomination;
import com.paylogic.paywalletlite.domain.token.enums.AllocationMode;
import com.paylogic.paywalletlite.domain.token.enums.TokenStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.token.TokenAllocationConfigRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Stratégie de génération de tokens basée sur une fenêtre glissante (sliding window)
 * avec seuil de densité configurable.
 *
 * Traduction Java de l'algorithme Python fourni.
 */
@Component
public class SlidingWindowTokenGenerator implements TokenGenerationStrategy {

    private static final String STRATEGY_NAME = "SLIDING_WINDOW_DENSITY";
    private static final int NONCE_BYTES = 16;
    private static final String SIGNATURE_ALGORITHM = "ECDSA_SHA256";
    private static final int DEFAULT_TOKEN_LIFETIME_HOURS = 72;
    private static final int DEFAULT_MAX_TRANSFERS = 3;

    private final TokenAllocationConfigRepository configRepository;
    private final SecureRandom secureRandom;

    @Autowired
    public SlidingWindowTokenGenerator(TokenAllocationConfigRepository configRepository) {
        this.configRepository = configRepository;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public List<Token> generateTokens(Wallet wallet, BigDecimal totalAmount) {
        // Récupérer la configuration active pour le type de wallet
        TokenAllocationConfig config = resolveConfig(wallet);

        System.out.println("CONFIG FOUND !"+ config.getConfigName());

        // Récupérer les dénominations triées par priorité décroissante
        List<BigDecimal> denominations = config.getSortedDenominationValues();

        System.out.println("DENOMNINATIONS LOADED !" + denominations.size());

        if (denominations.isEmpty()) {
            throw new BusinessException("No active denominations found for wallet type: "
                    + wallet.getWalletConfig().getWalletType());
        }

        // Paramètres de la stratégie
        int windowSize = config.getSlidingWindowSize();
        BigDecimal densityThreshold =  new BigDecimal(denominations.size())
                .divide(new BigDecimal(windowSize), 10, RoundingMode.HALF_UP);

        // Calculer le nombre de tokens par dénomination via sliding window
        List<TokenCount> tokenCounts = calculateTokenDistribution(
                totalAmount,
                denominations,
                windowSize = 3,
                densityThreshold
        );

        System.out.println("DISTRIBUTION CALCULATED AND TOKENS GENERATED !" + tokenCounts.size());

        // Générer les entités Token
        List<Token> tokens = new ArrayList<>();
        for (TokenCount tc : tokenCounts) {
            for (int i = 0; i < tc.count; i++) {
                Token token = buildToken(wallet, tc.denomination, config);
                tokens.add(token);
            }
        }

        // Validation finale
        validateGeneratedTokens(tokens, totalAmount, config);

        return tokens;
    }

    @Override
    public boolean supports(Wallet wallet) {
        return wallet.getWalletConfig() != null
                && wallet.getWalletConfig().getWalletType() != null;
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    // ============================================================
    // ALGORITHME SLIDING WINDOW (traduction Python → Java)
    // ============================================================

    /**
     * Calcule la distribution des tokens par dénomination.
     *
     * Algorithme :
     * 1. Parcourir les dénominations avec une fenêtre glissante
     * 2. Pour chaque dénomination dans la fenêtre, vérifier si currentAmount/d > p
     * 3. Si oui, ajouter un token de cette dénomination
     * 4. Si non, glisser la fenêtre
     * 5. À la fin, solder le reste avec la plus petite dénomination
     */
    private List<TokenCount> calculateTokenDistribution(
            BigDecimal totalAmount,
            List<BigDecimal> denominations,
            int windowSize,
            BigDecimal densityThreshold) {

        List<TokenCount> result = new ArrayList<>();
        BigDecimal currentAmount = totalAmount;

        int startIdx = 0;
        BigDecimal p = densityThreshold;

        // Initialiser le compteur pour chaque dénomination
        int[] counts = new int[denominations.size()];

        while (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
            // Définir la sous-liste (fenêtre)
            int endIdx = Math.min(startIdx + windowSize, denominations.size());
            List<BigDecimal> subList = denominations.subList(startIdx, endIdx);

            boolean tokenAddedInPass = false;

            for (int i = 0; i < subList.size(); i++) {
                BigDecimal d = subList.get(i);
                int globalIdx = startIdx + i;

                // Condition : currentAmount / d > p  (ratio > seuil de densité)
                if (d.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal ratio = currentAmount.divide(d, 10, RoundingMode.HALF_UP);

                    if (ratio.compareTo(p) > 0) {
                        counts[globalIdx]++;
                        currentAmount = currentAmount.subtract(d);
                        tokenAddedInPass = true;
                        // Break pour recommencer depuis le début de la sous-liste
                        break;
                    }
                }
            }

            // Glissement de fenêtre si aucun token ajouté
            if (!tokenAddedInPass) {
                if (startIdx + 1 < denominations.size()) {
                    startIdx++;
                } else {
                    // On est à la fin : solder avec la plus petite dénomination
                    if (!denominations.isEmpty()) {
                        BigDecimal lastDenomination = denominations.get(denominations.size() - 1);

                        if (lastDenomination.compareTo(BigDecimal.ZERO) > 0) {
                            int count = currentAmount.divide(lastDenomination, 0, RoundingMode.FLOOR).intValue();

                            if (count > 0) {
                                counts[denominations.size() - 1] += count;
                                currentAmount = currentAmount.subtract(
                                        lastDenomination.multiply(new BigDecimal(count))
                                );
                            }
                        }

                        // Si reste non nul, l'ajouter comme token de la plus petite dénomination
                        if (currentAmount.compareTo(BigDecimal.ZERO) > 0) {
                            counts[denominations.size() - 1]++;
                            currentAmount = BigDecimal.ZERO;
                        }
                    } else {
                        currentAmount = BigDecimal.ZERO;
                    }
                }
            }
        }

        // Construire le résultat (filtrer les compteurs > 0)
        for (int i = 0; i < denominations.size(); i++) {
            if (counts[i] > 0) {
                result.add(new TokenCount(denominations.get(i), counts[i]));
            }
        }

        return result;
    }

    // ============================================================
    // CONSTRUCTION DU TOKEN
    // ============================================================

// Dans buildToken() — retirer la logique de signature qui est maintenant dans TokenSignatureService

    private Token buildToken(Wallet wallet, BigDecimal value, TokenAllocationConfig config) {
        Token token = new Token();

        token.setValue(value);
        token.setIssuerId(wallet.getUser().getUserId());
        token.setIssuerWallet(wallet);
        token.setOriginalWallet(wallet);
        token.setCurrentHolderWallet(wallet);
        token.setIssuedAt(LocalDateTime.now());

        token.setExpiresAt(LocalDateTime.now().plusHours(
                config.getTokenLifetimeHours() != null ? config.getTokenLifetimeHours() : DEFAULT_TOKEN_LIFETIME_HOURS
        ));
        token.setNonce(generateNonce());
        token.setStatus(TokenStatus.CREATED); // Sera signé puis passé à ALLOCATED par TokenServiceImpl
        token.setAllocationMode(AllocationMode.EXPLICIT_RESERVATION);
        token.setTransferCount(0);
        token.setMaxTransfers(
                config.getMaxTransfersPerToken() != null ? config.getMaxTransfersPerToken() : DEFAULT_MAX_TRANSFERS
        );

        // Hash du token (pour intégrité, pas pour signature)
        String tokenData =
                token.getNonce()
                + "|" + token.getStatus().toString()
                + "|" + value.toPlainString()
                + "|" + wallet.getWalletId().toString()
                + "|" + token.getIssuedAt().toString();
        token.setTokenHash(HashUtil.sha256(tokenData));

        return token;
    }

    // ============================================================
    // VALIDATIONS MÉTIER
    // ============================================================

    private void validateGeneratedTokens(List<Token> tokens, BigDecimal expectedTotal, TokenAllocationConfig config) {
        // Vérifier le nombre max de tokens
        if (tokens.size() > config.getMaxTokenCount()) {
            throw new BusinessException(String.format(
                    "Generated token count (%d) exceeds maximum allowed (%d) for config '%s'",
                    tokens.size(), config.getMaxTokenCount(), config.getConfigName()
            ));
        }

        // Vérifier la valeur totale
        BigDecimal actualTotal = tokens.stream()
                .map(Token::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (actualTotal.compareTo(expectedTotal) != 0) {
            throw new BusinessException(String.format(
                    "Token total value mismatch: expected %s, got %s",
                    expectedTotal.toPlainString(), actualTotal.toPlainString()
            ));
        }

        // Vérifier les contraintes par token
        for (Token token : tokens) {
            /*
            if (token.getValue().compareTo(config.getMinSingleTokenValue()) < 0) {
                throw new BusinessException(String.format(
                        "Token value %s below minimum %s",
                        token.getValue().toPlainString(),
                        config.getMinSingleTokenValue().toPlainString()
                ));
            }*/
            if (token.getValue().compareTo(config.getMaxSingleTokenValue()) > 0) {
                throw new BusinessException(String.format(
                        "Token value %s above maximum %s",
                        token.getValue().toPlainString(),
                        config.getMaxSingleTokenValue().toPlainString()
                ));
            }
        }

        // Vérifier l'unicité des nonces
        long uniqueNonces = tokens.stream()
                .map(Token::getNonce)
                .distinct()
                .count();

        if (uniqueNonces != tokens.size()) {
            throw new BusinessException("Nonce collision detected during token generation");
        }
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private TokenAllocationConfig resolveConfig(Wallet wallet) {
        List<TokenAllocationConfig> configs = configRepository.findByWalletType(
                wallet.getWalletConfig().getWalletType()
        );

        return configs.stream()
                .filter(c -> c.getStatus() != null
                        && c.getStatus().name().equals("ACTIVE"))
                .findFirst()
                .orElseThrow(() -> new BusinessException(
                        "No active allocation config found for wallet type: "
                                + wallet.getWalletConfig().getWalletType()
                ));
    }

    private String generateNonce() {
        byte[] nonceBytes = new byte[NONCE_BYTES];
        secureRandom.nextBytes(nonceBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);
    }

    // ============================================================
    // CLASSE INTERNE : Compteur de tokens par dénomination
    // ============================================================

    private static class TokenCount {
        final BigDecimal denomination;
        final int count;

        TokenCount(BigDecimal denomination, int count) {
            this.denomination = denomination;
            this.count = count;
        }

        @Override
        public String toString() {
            return count + " x " + denomination.toPlainString() + " MAD";
        }
    }
}