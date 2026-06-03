package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.TokenDenomination;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenDenominationRepository {
    TokenDenomination save(TokenDenomination denomination);
    Optional<TokenDenomination> findById(UUID denominationId);
    Optional<TokenDenomination> findByValueAndCurrencyCode(java.math.BigDecimal value, String currencyCode);
    List<TokenDenomination> findAllActive();
    List<TokenDenomination> findByCurrencyCode(String currencyCode);
    List<TokenDenomination> findByCurrencyCodeOrdered(String currencyCode);
    void delete(TokenDenomination denomination);
    boolean existsByValueAndCurrencyCode(java.math.BigDecimal value, String currencyCode);
}