package com.paylogic.paywalletlite.repository.token;

import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.enums.OfflineTokenValidationStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfflineTransactionTokenRepository {

    @Transactional
    OfflineTransactionToken save(OfflineTransactionToken token);

    Optional<OfflineTransactionToken> findById(UUID id);

    List<OfflineTransactionToken> findAll();

    void delete(OfflineTransactionToken token);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    long count();

    /** Trouve tous les tokens d'une transaction */
    List<OfflineTransactionToken> findByTransactionId(UUID transactionId);

    /** Trouve un token spécifique dans une transaction */
    Optional<OfflineTransactionToken> findByTransactionIdAndTokenId(
            UUID transactionId, UUID tokenId);

    /** Trouve tous les tokens en attente de validation */
    List<OfflineTransactionToken> findByTransactionIdAndValidationStatus(
            UUID transactionId, OfflineTokenValidationStatus status);

    /** Vérifie si un token a déjà été soumis (anti-double-spend) */
    boolean existsByTokenIdAndValidationStatus(UUID tokenId,
                                               OfflineTokenValidationStatus status);

    /** Compte les tokens par statut de validation */
    long countByTransactionIdAndValidationStatus(UUID transactionId,
                                                 OfflineTokenValidationStatus status);
}