package com.paylogic.paywalletlite.service.audit;

import com.paylogic.paywalletlite.domain.notification.AuditLog;
import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service centralisé d'audit.
 *
 * Remplace les logger.info() dispersés par un système structuré
 * avec chaînage de hash pour l'intégrité et l'immuabilité.
 */
public interface AuditService {

    /**
     * Enregistre un événement d'audit avec détails au format JSON.
     *
     * @param eventType Type d'événement
     * @param actorId   ID de l'acteur (utilisateur, système, admin)
     * @param actorType Type d'acteur ("USER", "SYSTEM", "ADMIN", "DEVICE")
     * @param targetId  ID de l'entité cible (transaction, wallet, token)
     * @param targetType Type d'entité cible ("TRANSACTION", "WALLET", "TOKEN")
     * @param action    Description de l'action
     * @param details   Map des détails (convertie en JSON)
     */
    AuditLog logEvent(AuditEventType eventType,
                      UUID actorId,
                      String actorType,
                      UUID targetId,
                      String targetType,
                      String action,
                      Map<String, Object> details);

    /**
     * Version simplifiée sans détails.
     */
    AuditLog logEvent(AuditEventType eventType,
                      UUID actorId,
                      String actorType,
                      UUID targetId,
                      String targetType,
                      String action);

    /**
     * Récupère les événements d'audit pour une entité cible.
     */
    List<AuditLog> findByTargetId(UUID targetId);

    /**
     * Récupère les événements d'audit par type.
     */
    List<AuditLog> findByEventType(AuditEventType eventType);

    /**
     * Vérifie l'intégrité de la chaîne d'audit.
     */
    boolean verifyChainIntegrity();

    /**
     * Récupère les événements récents (limite configurable).
     */
    List<AuditLog> findRecentEvents(int limit);

    /**
     * Audit pour un overpayment.
     */
    AuditLog logOverpayment(UUID transactionId, UUID senderId, BigDecimal amount,
                                   AuditEventType eventType);


    AuditLog logSync(UUID batchId, UUID walletId, AuditEventType eventType,
                     int successCount, int failureCount, String details);

    /**
     * Audit pour une transaction.
     */
    AuditLog logTransaction(UUID transactionId, UUID senderId, UUID receiverId,
                                   AuditEventType eventType, String action);

    AuditLog logFraud(UUID transactionId, UUID walletId, String alertType, String description);

    AuditLog logToken(UUID tokenId, UUID walletId, AuditEventType eventType, String action);


}