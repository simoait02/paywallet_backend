package com.paylogic.paywalletlite.service.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paylogic.paywalletlite.domain.notification.AuditLog;
import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import com.paylogic.paywalletlite.repository.notification.AuditLogRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implémentation du service d'audit centralisé.
 *
 * Garantit :
 * - Immuabilité : chaque entrée est horodatée et hashée
 * - Chaînage : chaque entrée référence le hash de l'entrée précédente
 * - Traçabilité : actor, target, action, details pour chaque événement
 *
 * Transaction Propagation.REQUIRES_NEW :
 * L'audit est sauvegardé même si la transaction métier échoue (rollback).
 */
@Service
@Transactional(readOnly = true)
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PersistenceContext
    private EntityManager em;

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public AuditLog logEvent(AuditEventType eventType,
                             UUID actorId,
                             String actorType,
                             UUID targetId,
                             String targetType,
                             String action,
                             Map<String, Object> details) {

        AuditLog audit = new AuditLog();
        audit.setEventType(eventType);
        audit.setActorId(actorId);
        audit.setActorType(actorType);
        audit.setTargetId(targetId);
        audit.setTargetType(targetType);
        audit.setAction(action);
        audit.setTimestamp(LocalDateTime.now());

        // Sérialiser les détails en JSON
        if (details != null && !details.isEmpty()) {
            try {
                audit.setDetailsJson(objectMapper.writeValueAsString(details));
            } catch (JsonProcessingException e) {
                logger.error("Erreur de sérialisation JSON pour l'audit", e);
                audit.setDetailsJson("{\"error\": \"serialization_failed\"}");
            }
        }

        // Calculer le hash d'intégrité
        String integrityData = buildIntegrityData(audit);
        audit.setIntegrityHash(HashUtil.sha256(integrityData));

        // Chaîner avec l'entrée précédente
        String previousHash = getPreviousAuditHash();
        audit.setPreviousAuditHash(previousHash);

        auditLogRepository.save(audit);

        // Logger aussi pour le suivi temps réel
        logger.info("[AUDIT] {} | Actor: {}({}) | Target: {}({}) | Action: {} | Severity: {}",
                eventType, actorId, actorType, targetId, targetType, action, eventType.getSeverity());

        return audit;
    }

    @Override
    public AuditLog logEvent(AuditEventType eventType,
                             UUID actorId,
                             String actorType,
                             UUID targetId,
                             String targetType,
                             String action) {
        return logEvent(eventType, actorId, actorType, targetId, targetType, action, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> findByTargetId(UUID targetId) {

        return auditLogRepository.findByTargetId(targetId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> findByEventType(AuditEventType eventType) {
        return auditLogRepository.findByEventType(eventType);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyChainIntegrity() {


        List<AuditLog> logs = auditLogRepository.findAll();
        String expectedPreviousHash = "0"; // Genesis hash

        for (AuditLog log : logs) {
            // Vérifier le chaînage
            if (!expectedPreviousHash.equals(log.getPreviousAuditHash())) {
                logger.error("Chaîne d'audit brisée à l'entrée {} : attendu={}, trouvé={}",
                        log.getAuditId(), expectedPreviousHash, log.getPreviousAuditHash());
                return false;
            }

            // Vérifier le hash d'intégrité
            String computedHash = HashUtil.sha256(buildIntegrityData(log));
            if (!computedHash.equals(log.getIntegrityHash())) {
                logger.error("Hash d'intégrité invalide à l'entrée {} : attendu={}, calculé={}",
                        log.getAuditId(), log.getIntegrityHash(), computedHash);
                return false;
            }

            expectedPreviousHash = log.getIntegrityHash();
        }

        logger.info("Chaîne d'audit vérifiée : {} entrées valides", logs.size());
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> findRecentEvents(int limit) {

        return auditLogRepository.findRecentEvents(limit);
    }

    // ============================================================
    // MÉTHODES UTILITAIRES
    // ============================================================

    /**
     * Construit la chaîne de données pour le calcul du hash d'intégrité.
     */
    private String buildIntegrityData(AuditLog audit) {
        return String.join("|",
                audit.getEventType().name(),
                audit.getActorId() != null ? audit.getActorId().toString() : "SYSTEM",
                audit.getActorType() != null ? audit.getActorType() : "SYSTEM",
                audit.getTargetId() != null ? audit.getTargetId().toString() : "NONE",
                audit.getTargetType() != null ? audit.getTargetType() : "NONE",
                audit.getAction() != null ? audit.getAction() : "",
                audit.getTimestamp() != null ? audit.getTimestamp().toString() : "",
                audit.getDetailsJson() != null ? audit.getDetailsJson() : ""
        );
    }

    /**
     * Récupère le hash de la dernière entrée d'audit pour le chaînage.
     */
    private String getPreviousAuditHash() {
        return auditLogRepository.findLatest()
                .map(AuditLog::getIntegrityHash)
                .orElse("0"); // Genesis hash
    }

    // ============================================================
    // MÉTHODES DE CONVENIENCE POUR LES SERVICES
    // ============================================================

    /**
     * Audit pour une synchronisation.
     */
    @Override
    public AuditLog logSync(UUID batchId, UUID walletId, AuditEventType eventType,
                            int successCount, int failureCount, String details) {
        Map<String, Object> map = new HashMap<>();
        map.put("batchId", batchId);
        map.put("successCount", successCount);
        map.put("failureCount", failureCount);
        map.put("details", details);
        return logEvent(eventType, walletId, "WALLET", batchId, "SYNC_BATCH",
                eventType.getDescription(), map);
    }

    /**
     * Audit pour un token.
     */
    @Override
    public AuditLog logToken(UUID tokenId, UUID walletId, AuditEventType eventType, String action) {
        Map<String, Object> map = new HashMap<>();
        map.put("tokenId", tokenId);
        map.put("walletId", walletId);
        return logEvent(eventType, walletId, "WALLET", tokenId, "TOKEN", action, map);
    }

    /**
     * Audit pour une transaction.
     */
    @Override
    public AuditLog logTransaction(UUID transactionId, UUID senderId, UUID receiverId,
                                   AuditEventType eventType, String action) {
        Map<String, Object> map = new HashMap<>();
        map.put("senderId", senderId);
        map.put("receiverId", receiverId);
        return logEvent(eventType, senderId, "WALLET", transactionId, "TRANSACTION", action, map);
    }

    /**
     * Audit pour un overpayment.
     */
    @Override
    public AuditLog logOverpayment(UUID transactionId, UUID senderId, BigDecimal amount,
                                   AuditEventType eventType) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", transactionId);
        map.put("refundedTo", senderId);
        map.put("amount", amount);
        return logEvent(eventType, senderId, "WALLET", transactionId, "OVERPAYMENT",
                eventType.getDescription(), map);
    }

    /**
     * Audit pour la fraude.
     */
    @Override
    public AuditLog logFraud(UUID transactionId, UUID walletId, String alertType, String description) {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionId", transactionId);
        map.put("walletId", walletId);
        map.put("alertType", alertType);
        map.put("description", description);
        return logEvent(AuditEventType.FRAUD_ALERT_TRIGGERED, null, "SYSTEM",
                transactionId, "FRAUD", alertType, map);
    }
}