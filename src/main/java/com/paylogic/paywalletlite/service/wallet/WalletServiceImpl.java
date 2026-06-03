package com.paylogic.paywalletlite.service.wallet;

import com.paylogic.paywalletlite.domain.identity.User;
import com.paylogic.paywalletlite.domain.notification.enums.AuditEventType;
import com.paylogic.paywalletlite.domain.token.TokenAllocationConfig;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.domain.wallet.WalletConfig;
import com.paylogic.paywalletlite.domain.wallet.enums.*;
import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.wallet.WalletKeyPair;
import com.paylogic.paywalletlite.dto.request.WalletConfigCreateRequestDto;
import com.paylogic.paywalletlite.dto.request.WalletConfigUpdateRequestDto;
import com.paylogic.paywalletlite.mapper.WalletConfigMapper;
import com.paylogic.paywalletlite.repository.wallet.WalletConfigRepository;
import com.paylogic.paywalletlite.repository.wallet.WalletKeyPairRepository;
import com.paylogic.paywalletlite.security.crypto.AesEncryptionUtil;
import com.paylogic.paywalletlite.security.crypto.KeyGeneratorUtil;
import com.paylogic.paywalletlite.service.security.CertificateService;
import com.paylogic.paywalletlite.service.security.CryptographicService;
import com.paylogic.paywalletlite.dto.request.CreateWalletRequestDto;
import com.paylogic.paywalletlite.dto.response.WalletResponseDto;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.wallet.WalletRepository;
import com.paylogic.paywalletlite.service.audit.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletConfigRepository walletConfigRepository;
    private final WalletConfigService walletConfigService;
    private final WalletConfigMapper walletConfigMapper;
    private final CryptographicService cryptographicService;
    private final AuditService auditService;
    private final CertificateService certificateService;

    // AJOUTER dans les champs
    private final WalletKeyPairRepository walletKeyPairRepository;
    private final KeyGeneratorUtil keyGeneratorUtil;
    private final AesEncryptionUtil aesEncryptionUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             WalletConfigRepository walletConfigRepository,
                             WalletConfigService walletConfigService,
                             WalletConfigMapper walletConfigMapper,
                             CryptographicService cryptographicService,
                             AuditService auditService,
                             WalletKeyPairRepository walletKeyPairRepository,
                             KeyGeneratorUtil keyGeneratorUtil,
                             AesEncryptionUtil aesEncryptionUtil,
                             CertificateService certificateService) {
        this.walletRepository = walletRepository;
        this.walletConfigRepository = walletConfigRepository;
        this.cryptographicService = cryptographicService;
        this.auditService = auditService;
        this.walletKeyPairRepository = walletKeyPairRepository;
        this.keyGeneratorUtil = keyGeneratorUtil;
        this.aesEncryptionUtil = aesEncryptionUtil;
        this.walletConfigService = walletConfigService;
        this.walletConfigMapper = walletConfigMapper;
        this.certificateService = certificateService;
    }

    @Override
    @Transactional
    public WalletResponseDto requestWalletCreation(UUID userId, CreateWalletRequestDto request) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new BusinessException("User not found with id: " + userId);
        }

        long walletCount = walletRepository.countByUserId(userId);
        if (walletCount >= 5) {
            throw new BusinessException("Maximum wallet limit reached for user");
        }

        // Get or create config
        WalletConfig config;
        if (request.getConfigId() != null) {
            config = walletConfigService.getConfigById(request.getConfigId())
                    .orElseThrow(() -> new BusinessException("Config not found: " + request.getConfigId()));
        } else {
            WalletConfigCreateRequestDto configDto = new WalletConfigCreateRequestDto();
            configDto.setWalletType(request.getWalletType());
            config = walletConfigService.createConfig(configDto);
        }

        // 🔥 RÉCUPÉRER L'ENTITÉ TokenAllocationConfig DEPUIS LA BASE
        TokenAllocationConfig tokenAllocationConfig = entityManager.find(
                TokenAllocationConfig.class,
                UUID.fromString("c3616d30-92a6-4acc-8b0c-2add485390ec")
        );

        if (tokenAllocationConfig == null) {
            throw new BusinessException("TokenAllocationConfig not found");
        }

        Wallet wallet = new Wallet();

        // 🔥 SETTER LES RELATIONS JPA (pas seulement les UUID)
        wallet.setUser(user);                    // ← Relation JPA
        wallet.setWalletConfig(config);           // ← Relation JPA
        wallet.setTokenAllocationConfig(tokenAllocationConfig); // ← Relation JPA
        wallet.setCurrency(request.getWalletCurrency());

        wallet.setStatus(WalletStatus.PENDING_APPROVAL);
        wallet.setOnlineBalance(BigDecimal.ZERO);
        wallet.setOfflineBalance(BigDecimal.ZERO);
        wallet.setPendingBalance(BigDecimal.ZERO);

        System.out.println("Nouvelle Wallet : " + wallet);

        Wallet savedWallet = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_CREATED,
                userId,
                "USER",
                savedWallet.getWalletId(),
                "WALLET",
                "Wallet creation requested with type: " + request.getWalletType()
        );

        return mapToResponseDto(savedWallet);
    }

    @Override
    public WalletResponseDto getWalletById(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));
        return mapToResponseDto(wallet);
    }

    @Override
    public Wallet findById(UUID walletId) throws BusinessException {
        return walletRepository.findById(walletId).get();
    }

    @Override
    public List<WalletResponseDto> getWalletsByUserId(UUID userId) {
        return walletRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<WalletResponseDto> getPendingWallets() {
        return walletRepository.findByStatus(WalletStatus.PENDING_APPROVAL)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WalletResponseDto approveWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() != WalletStatus.PENDING_APPROVAL) {
            throw new BusinessException("Wallet is not pending approval. Current status: " + wallet.getStatus());
        }


        // ============================================================
        // 1. GENERATION PAIRE DE CLES ECDSA POUR LE WALLET
        // ============================================================
        KeyGeneratorUtil.KeyPairEncoded keyPair = keyGeneratorUtil.generateEncodedKeyPair();

        // ============================================================
        // 2. CHIFFREMENT DE LA CLE PRIVEE
        // ============================================================
        String encryptedPrivateKey = aesEncryptionUtil.encrypt(keyPair.getPrivateKeyBase64());

        System.out.println("KEY PAIR { PRIV :  "+ keyPair.getPrivateKeyBase64() + " PUB: "+ keyPair.getPublicKeyBase64());

        // ============================================================
        // 3. STOCKAGE DU KEYPAIR
        // ============================================================
        WalletKeyPair walletKeyPair = new WalletKeyPair();
        walletKeyPair.setWallet(wallet);
        walletKeyPair.setPublicKey(keyPair.getPublicKeyBase64());
        walletKeyPair.setPrivateKeyEncrypted(encryptedPrivateKey);
        walletKeyPair.setKeyAlgorithm("ECDSA_P256");
        walletKeyPair.setStorageType(KeyStorageType.SERVER_ENCRYPTED);
        walletKeyPair.setExpiresAt(LocalDateTime.now().plusDays(365));
        walletKeyPair.setStatus(KeyStatus.ACTIVE);

        System.out.println("Wallet : "+ walletKeyPair.getPublicKey());

        // Signature du serveur sur la clé publique (preuve d'émission)
        try {
            ServerKey signingKey = cryptographicService.getActiveKey(ServerKeyPurpose.TOKEN_SIGNING);
            String keyData = wallet.getWalletId().toString() + "|" + keyPair.getPublicKeyBase64();
            String serverSignature = cryptographicService.signData(keyData, signingKey.getServerKeyId());
            walletKeyPair.setServerIssuanceSignature(serverSignature);
        } catch (BusinessException e) {
            // Si pas de clé de signing dispo, continuer sans signature
            walletKeyPair.setServerIssuanceSignature("PENDING_SIGNATURE");
        }

        walletKeyPairRepository.save(walletKeyPair);

        // ============================================================
        // 4. MISE A JOUR DU WALLET
        // ============================================================
        wallet.setPublicKey(keyPair.getPublicKeyBase64());
        wallet.setCurrentKeypairId(walletKeyPair.getKeypairId());
        wallet.setStatus(WalletStatus.APPROVED);

        Wallet updated = walletRepository.save(wallet);

        // ============================================================
        // 5. AUDIT LOG
        // ============================================================
        auditService.logEvent(
                AuditEventType.WALLET_APPROVED,
                null,
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet approved, keypair generated: " + walletKeyPair.getKeypairId()
        );

        return mapToResponseDto(updated);
    }

    /**
     * POST /api/v1/admin/wallets/{walletId}/rotate-key
     * Effectue la rotation de la clé d'un wallet.
     */
    @Transactional
    public WalletResponseDto rotateWalletKey(UUID walletId, String reason) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        // 1. Récupérer l'ancienne clé active
        WalletKeyPair oldKey = walletKeyPairRepository.findActiveByWalletId(walletId)
                .orElseThrow(() -> new BusinessException("No active key found for wallet: " + walletId));

        // 2. Marquer l'ancienne clé comme expirée
        oldKey.setStatus(KeyStatus.EXPIRED);
        oldKey.setRotatedAt(LocalDateTime.now());
        oldKey.setRotationReason(reason);
        walletKeyPairRepository.save(oldKey);

        // 3. Générer nouvelle paire
        KeyGeneratorUtil.KeyPairEncoded newKeyPair = keyGeneratorUtil.generateEncodedKeyPair();
        String encryptedPrivateKey = aesEncryptionUtil.encrypt(newKeyPair.getPrivateKeyBase64());

        // 4. Créer nouveau keypair
        WalletKeyPair newWalletKeyPair = new WalletKeyPair();
        newWalletKeyPair.setKeypairId(UUID.randomUUID());
        newWalletKeyPair.setWallet(wallet);
        newWalletKeyPair.setPublicKey(newKeyPair.getPublicKeyBase64());
        newWalletKeyPair.setPrivateKeyEncrypted(encryptedPrivateKey);
        newWalletKeyPair.setKeyAlgorithm("ECDSA_P256");
        newWalletKeyPair.setStorageType(KeyStorageType.SERVER_ENCRYPTED);
        newWalletKeyPair.setExpiresAt(LocalDateTime.now().plusDays(365));
        newWalletKeyPair.setStatus(KeyStatus.ACTIVE);

        walletKeyPairRepository.save(newWalletKeyPair);
        // 5. Mettre à jour le wallet
        wallet.setPublicKey(newWalletKeyPair.getPublicKey());
        wallet.setCurrentKeypairId(newWalletKeyPair.getKeypairId());
        walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.KEY_ROTATION,
                null,
                "ADMIN",
                walletId,
                "WALLET",
                "Key rotated. Old: " + oldKey.getKeypairId() + ", New: " + newWalletKeyPair.getKeypairId()
        );

        return mapToResponseDto(wallet);
    }

    public List<WalletKeyPair> getWalletKeyHistory(UUID walletId) {
        return walletKeyPairRepository.findByWalletId(walletId);
    }

    // ============================================================
    // WORKFLOW APPROBATION
    // ============================================================


    @Override
    @Transactional
    public WalletResponseDto rejectWallet(UUID walletId, String reason) throws BusinessException {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException("Rejection reason is required");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() != WalletStatus.PENDING_APPROVAL) {
            throw new BusinessException("Wallet must be in PENDING_APPROVAL status to be rejected. Current: " + wallet.getStatus());
        }

        wallet.setStatus(WalletStatus.CLOSED);
        wallet.setRejectionReason(reason);
        Wallet saved = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_REJECTED,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet rejected. Reason: " + reason
        );

        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public WalletResponseDto configureWallet(UUID walletId, WalletConfigUpdateRequestDto configDto)
            throws BusinessException {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        // Vérifier le statut du wallet
        if (wallet.getStatus() != WalletStatus.APPROVED || wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException(
                    "Wallet must be APPROVED or ACTIVE before configuration. Current status: " + wallet.getStatus()
            );
        }

        WalletConfig config;

        if (configDto.isUsingExistingConfig()) {
            // ============================================================
            // OPTION 1 : Réutiliser une configuration existante
            // ============================================================
            config = walletConfigRepository.findById(configDto.getExistingConfigId())
                    .orElseThrow(() -> new BusinessException(
                            "WalletConfig not found: " + configDto.getExistingConfigId()
                    ));

            // Vérifier que la config est active
            if (config.getStatus() != WalletConfigStatus.ACTIVE) {
                throw new BusinessException(
                        "Cannot use inactive WalletConfig. Status: " + config.getStatus()
                );
            }

            // ============================================================
            // VÉRIFICATION DE COMPATIBILITÉ : WalletType doit correspondre
            // ============================================================
            if (config.getWalletType() != wallet.getWalletConfig().getWalletType()) {
                throw new BusinessException(
                        String.format(
                                "Wallet type mismatch: cannot attach a %s config to a %s wallet. "
                                        + "Please use a config with matching wallet type or create a new one.",
                                config.getWalletType(),
                                wallet.getWalletConfig().getWalletType()
                        )
                );
            }

            // Désactiver l'ancienne config si elle existe
            if (wallet.getWalletConfig() != null &&
                    !wallet.getWalletConfig().getConfigId().equals(config.getConfigId())) {

                WalletConfig oldConfig = wallet.getWalletConfig();
                oldConfig.setStatus(WalletConfigStatus.DEPRECATED);
                oldConfig.setUpdatedAt(LocalDateTime.now());
                walletConfigRepository.save(oldConfig);
            }

            auditService.logEvent(
                    AuditEventType.WALLET_CREATED,
                    wallet.getUserId(),
                    "ADMIN",
                    walletId,
                    "WALLET",
                    String.format(
                            "Wallet type %s configured with existing config %s (type: %s)",
                            wallet.getWalletConfig().getWalletType(),
                            configDto.getExistingConfigId(),
                            config.getWalletType()
                    )
            );

        } else {
            // ============================================================
            // OPTION 2 : Créer une nouvelle configuration
            // ============================================================
            if (configDto.getWalletType() == null) {
                throw new BusinessException(
                        "walletType is required when creating a new configuration"
                );
            }

            // Désactiver l'ancienne config si elle existe
            if (wallet.getWalletConfig() != null) {
                WalletConfig oldConfig = wallet.getWalletConfig();
                oldConfig.setStatus(WalletConfigStatus.DEPRECATED);
                oldConfig.setUpdatedAt(LocalDateTime.now());
                walletConfigRepository.save(oldConfig);
            }

            // Créer la nouvelle config avec valeurs par défaut si null
            config = new WalletConfig();
            config.setWalletType(configDto.getWalletType());
            config.setDailySpendingLimit(
                    configDto.getDailySpendingLimit() != null
                            ? configDto.getDailySpendingLimit()
                            : getDefaultDailyLimit(configDto.getWalletType())
            );
            config.setMonthlySpendingLimit(
                    configDto.getMonthlySpendingLimit() != null
                            ? configDto.getMonthlySpendingLimit()
                            : getDefaultMonthlyLimit(configDto.getWalletType())
            );
            config.setMaxSingleTransaction(
                    configDto.getMaxSingleTransaction() != null
                            ? configDto.getMaxSingleTransaction()
                            : getDefaultMaxTransaction(configDto.getWalletType())
            );
            config.setMaxOfflineBalance(
                    configDto.getMaxOfflineBalance() != null
                            ? configDto.getMaxOfflineBalance()
                            : getDefaultMaxOffline(configDto.getWalletType())
            );
            config.setKeyRotationPeriodDays(
                    configDto.getKeyRotationPeriodDays() != null
                            ? configDto.getKeyRotationPeriodDays()
                            : 90
            );
            config.setRequiresBiometricForOffline(
                    configDto.getRequiresBiometricForOffline() != null
                            ? configDto.getRequiresBiometricForOffline()
                            : false
            );
            config.setPinMaxAttempts(
                    configDto.getPinMaxAttempts() != null
                            ? configDto.getPinMaxAttempts()
                            : 3
            );
            config.setOfflineTransactionTimeoutMinutes(
                    configDto.getOfflineTransactionTimeoutMinutes() != null
                            ? configDto.getOfflineTransactionTimeoutMinutes()
                            : 30
            );
            config.setAllowTokenTransfer(
                    configDto.getAllowTokenTransfer() != null
                            ? configDto.getAllowTokenTransfer()
                            : true
            );
            config.setAllowMerchantPayment(
                    configDto.getAllowMerchantPayment() != null
                            ? configDto.getAllowMerchantPayment()
                            : true
            );
            config.setStatus(WalletConfigStatus.ACTIVE);
            config.setCreatedAt(LocalDateTime.now());
            config.setUpdatedAt(LocalDateTime.now());

            config = walletConfigRepository.save(config);

            auditService.logEvent(
                    AuditEventType.WALLET_CONFIGURED,
                    wallet.getUserId(),
                    "ADMIN",
                    walletId,
                    "WALLET",
                    "Wallet configured with new config type: " + configDto.getWalletType()
            );
        }

        // Attacher la config au wallet (commun aux deux options)
        wallet.setWalletConfig(config);
        wallet.setWalletConfigId(config.getConfigId());
        Wallet savedWallet = walletRepository.save(wallet);

        return mapToResponseDto(savedWallet);
    }

    @Override
    @Transactional
    public WalletResponseDto activateWallet(UUID walletId) throws BusinessException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() != WalletStatus.APPROVED) {
            throw new BusinessException("Wallet must be APPROVED before activation. Current: " + wallet.getStatus());
        }

        if (wallet.getWalletConfig() == null) {
            throw new BusinessException("Wallet must be configured before activation");
        }

        wallet.setStatus(WalletStatus.ACTIVE);
        certificateService.issueCertificate(wallet.getWalletId(), UUID.fromString("7260ac78-7165-489a-884a-d1327b5c2a3c"));
        Wallet saved = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_APPROVED,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet activated and ready for use"
        );

        return mapToResponseDto(saved);
    }

    // ============================================================
    // GESTION DES STATUTS
    // ============================================================

    @Override
    @Transactional
    public WalletResponseDto lockWallet(UUID walletId, String reason) throws BusinessException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() != WalletStatus.ACTIVE && wallet.getStatus() != WalletStatus.APPROVED) {
            throw new BusinessException("Cannot lock wallet with status: " + wallet.getStatus());
        }

        wallet.setStatus(WalletStatus.LOCKED);
        Wallet saved = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_LOCKED,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet locked. Reason: " + (reason != null ? reason : "none")
        );

        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public WalletResponseDto unlockWallet(UUID walletId) throws BusinessException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() != WalletStatus.LOCKED) {
            throw new BusinessException("Wallet must be LOCKED to be unlocked. Current: " + wallet.getStatus());
        }

        wallet.setStatus(WalletStatus.ACTIVE);
        Wallet saved = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_UNLOCKED,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet unlocked and restored to ACTIVE"
        );

        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public WalletResponseDto freezeWallet(UUID walletId, String reason) throws BusinessException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException("Cannot freeze wallet with status: " + wallet.getStatus());
        }

        wallet.setStatus(WalletStatus.FROZEN);
        Wallet saved = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_FROZEN,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet frozen. Reason: " + (reason != null ? reason : "none")
        );

        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public WalletResponseDto closeWallet(UUID walletId, String reason) throws BusinessException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        if (wallet.getStatus() == WalletStatus.CLOSED) {
            throw new BusinessException("Wallet is already closed");
        }

        // Vérifier que le solde est nul avant fermeture
        BigDecimal totalBalance = wallet.getOnlineBalance()
                .add(wallet.getOfflineBalance())
                .add(wallet.getPendingBalance());

        if (totalBalance.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Cannot close wallet with non-zero balance. Total: " + totalBalance);
        }

        wallet.setStatus(WalletStatus.CLOSED);
        Wallet saved = walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_CLOSED,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                "Wallet closed. Reason: " + (reason != null ? reason : "none")
        );

        return mapToResponseDto(saved);
    }

    @Override
    public WalletResponseDto getActiveWalletByUserId(UUID userId) throws BusinessException {
        List<Wallet> wallets = walletRepository.findByUserId(userId);

        Wallet activeWallet = wallets.stream()
                .filter(w -> w.getStatus() == WalletStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new BusinessException("No active wallet found for user: " + userId));

        return mapToResponseDto(activeWallet);
    }

    @Override
    @Transactional
    public WalletResponseDto fundWallet(UUID walletId, BigDecimal amount, CurrencyCode currency,  String fundingSource,
                                        String externalReference, String notes) throws BusinessException {
        // 1. Vérifier le montant
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Fund amount must be greater than zero. Provided: " + amount);
        }

        // 2. Récupérer le wallet
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        // 3. Vérifier le statut du wallet
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BusinessException(
                    "Cannot fund wallet. Wallet must be ACTIVE. Current status: " + wallet.getStatus()
            );
        }

        // 4. Vérifier la limite maximale de solde (optionnel, selon règles métier)
        WalletConfig config = wallet.getWalletConfig();
        if (config != null && config.getMonthlySpendingLimit() != null) {
            BigDecimal newBalance = wallet.getOnlineBalance().add(amount);
            if (newBalance.compareTo(config.getMonthlySpendingLimit()) > 0) {
                throw new BusinessException(
                        String.format(
                                "Fund amount exceeds maximum allowed balance. "
                                        + "Current: %.2f, Attempted: %.2f, Maximum: %.2f",
                                wallet.getOnlineBalance(), amount, config.getMonthlySpendingLimit()
                        )
                );
            }
        }

        // 5. Créditer le wallet
        BigDecimal oldBalance = wallet.getOnlineBalance();
        wallet.setOnlineBalance(oldBalance.add(amount));
        Wallet savedWallet = walletRepository.save(wallet);

        // 6. Journaliser l'événement d'audit
        auditService.logEvent(
                AuditEventType.WALLET_FUNDED,
                wallet.getUserId(),
                "ADMIN",
                walletId,
                "WALLET",
                String.format(
                        "Wallet funded: %.2f via %s. Balance: %.2f -> %.2f. Ref: %s  Currency ( %s )",
                        amount, fundingSource, oldBalance, savedWallet.getOnlineBalance(),
                        externalReference != null ? externalReference : "N/A", currency
                )
        );

        // 7. Envoyer une notification à l'utilisateur (si service disponible)
        // notificationService.sendWalletFundedNotification(wallet.getUserId(), amount);

        return mapToResponseDto(savedWallet);
    }

    // ============================================================
    // MÉTHODES BLOC 7.4 — TRANSACTION & SYNC
    // ============================================================

    @Override
    public boolean existsById(UUID walletId) {
        return walletRepository.findById(walletId).isPresent();
    }

    @Override
    public BigDecimal getOnlineBalance(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));
        return wallet.getOnlineBalance() != null ? wallet.getOnlineBalance() : BigDecimal.ZERO;
    }

    @Override
    @Transactional
    public void creditBalance(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Credit amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        BigDecimal currentBalance = wallet.getOnlineBalance() != null ? wallet.getOnlineBalance() : BigDecimal.ZERO;
        wallet.setOnlineBalance(currentBalance.add(amount));
        walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_CREDITED,
                wallet.getUserId(),
                "SYSTEM",
                walletId,
                "WALLET",
                "Balance credited: +" + amount + ". New balance: " + wallet.getOnlineBalance()
        );
    }

    @Override
    @Transactional
    public void debitBalance(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Debit amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        BigDecimal currentBalance = wallet.getOnlineBalance() != null ? wallet.getOnlineBalance() : BigDecimal.ZERO;
        if (currentBalance.compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds. Balance: " + currentBalance + ", Required: " + amount);
        }

        wallet.setOnlineBalance(currentBalance.subtract(amount));
        walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_DEBITED,
                wallet.getUserId(),
                "SYSTEM",
                walletId,
                "WALLET",
                "Balance debited: -" + amount + ". New balance: " + wallet.getOnlineBalance()
        );
    }


    @Override
    @Transactional
    public void creditPendingBalance(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Credit amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        BigDecimal currentPendingBalance = wallet.getPendingBalance() != null ? wallet.getPendingBalance() : BigDecimal.ZERO;
        wallet.setPendingBalance(currentPendingBalance.add(amount));
        walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_CREDITED,
                wallet.getUserId(),
                "SYSTEM",
                walletId,
                "WALLET",
                "Pending Balance credited: +" + amount + ". New pending balance: " + wallet.getPendingBalance()
        );
    }

    @Override
    @Transactional
    public void debitPendingBalance(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Debit amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        BigDecimal currentPendingBalance = wallet.getPendingBalance() != null ? wallet.getPendingBalance() : BigDecimal.ZERO;
        if (currentPendingBalance.compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds. Balance: " + currentPendingBalance + ", Required: " + amount);
        }

        wallet.setPendingBalance(currentPendingBalance.subtract(amount));
        walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.WALLET_DEBITED,
                wallet.getUserId(),
                "SYSTEM",
                walletId,
                "WALLET",
                "Pending Balance debited: -" + amount + ". New pending balance: " + wallet.getPendingBalance()
        );
    }

    @Override
    @Transactional
    public void recordCreditDebt(UUID walletId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Credit debt amount must be positive");
        }

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        // Enregistrer la dette dans le pending_balance (ou un champ dédié si tu en as un)
        BigDecimal currentPending = wallet.getPendingBalance() != null ? wallet.getPendingBalance() : BigDecimal.ZERO;
        wallet.setPendingBalance(currentPending.add(amount));
        walletRepository.save(wallet);

        auditService.logEvent(
                AuditEventType.CRECIT_DEBT_RECORDED,
                wallet.getUserId(),
                "SYSTEM",
                walletId,
                "WALLET",
                "Credit debt recorded: " + amount + ". Total pending: " + wallet.getPendingBalance()
        );
    }



    private WalletConfig createDefaultWalletConfig(WalletType type) {
        WalletConfig config = new WalletConfig();
        config.setWalletType(type);
        config.setStatus(WalletConfigStatus.ACTIVE);
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());

        switch (type) {
            case GOLD:
                config.setDailySpendingLimit(new BigDecimal("10000.00"));
                config.setMonthlySpendingLimit(new BigDecimal("100000.00"));
                config.setMaxSingleTransaction(new BigDecimal("5000.00"));
                config.setMaxOfflineBalance(new BigDecimal("2000.00"));
                break;
            case SILVER:
                config.setDailySpendingLimit(new BigDecimal("5000.00"));
                config.setMonthlySpendingLimit(new BigDecimal("50000.00"));
                config.setMaxSingleTransaction(new BigDecimal("2000.00"));
                config.setMaxOfflineBalance(new BigDecimal("1000.00"));
                break;
            case BASIC:
            default:
                config.setDailySpendingLimit(new BigDecimal("1000.00"));
                config.setMonthlySpendingLimit(new BigDecimal("10000.00"));
                config.setMaxSingleTransaction(new BigDecimal("500.00"));
                config.setMaxOfflineBalance(new BigDecimal("500.00"));
                break;
        }

        config.setKeyRotationPeriodDays(90);
        config.setRequiresBiometricForOffline(true);
        config.setPinMaxAttempts(3);
        config.setOfflineTransactionTimeoutMinutes(30);
        config.setAllowTokenTransfer(true);
        config.setAllowMerchantPayment(true);

        entityManager.persist(config);
        return config;
    }

    private WalletResponseDto mapToResponseDto(Wallet wallet) {
        WalletResponseDto dto = new WalletResponseDto();
        dto.setWalletId(wallet.getWalletId());

        // UUID direct (pas de relation JPA)
        dto.setUserId(wallet.getUserId());

        // Récupérer le type depuis WalletConfig en base
        WalletConfig config = entityManager.find(WalletConfig.class, wallet.getWalletConfigId());
        dto.setWalletType(config != null ? config.getWalletType() : null);
        dto.setConfig(walletConfigMapper.toDto(wallet.getWalletConfig()));
        dto.setStatus(wallet.getStatus());
        dto.setOnlineBalance(wallet.getOnlineBalance());
        dto.setOfflineBalance(wallet.getOfflineBalance());
        dto.setPendingBalance(wallet.getPendingBalance());
        dto.setCurrency(wallet.getCurrency() == null ? CurrencyCode.MAD : wallet.getCurrency());
        dto.setCreatedAt(wallet.getCreatedAt());
        dto.setPublicKey(wallet.getPublicKey());
        dto.setRejectionReason(wallet.getRejectionReason());
        return dto;
    }

    private BigDecimal getDefaultDailyLimit(WalletType type) {
        switch (type) {
            case GOLD: return new BigDecimal("10000.00");
            case SILVER: return new BigDecimal("5000.00");
            case BASIC: return new BigDecimal("2000.00");
            default: return new BigDecimal("1000.00");
        }
    }

    private BigDecimal getDefaultMonthlyLimit(WalletType type) {
        switch (type) {
            case GOLD: return new BigDecimal("100000.00");
            case SILVER: return new BigDecimal("50000.00");
            case BASIC: return new BigDecimal("20000.00");
            default: return new BigDecimal("10000.00");
        }
    }

    private BigDecimal getDefaultMaxTransaction(WalletType type) {
        switch (type) {
            case GOLD: return new BigDecimal("5000.00");
            case SILVER: return new BigDecimal("2000.00");
            case BASIC: return new BigDecimal("500.00");
            default: return new BigDecimal("100.00");
        }
    }

    private BigDecimal getDefaultMaxOffline(WalletType type) {
        switch (type) {
            case GOLD: return new BigDecimal("20000.00");
            case SILVER: return new BigDecimal("10000.00");
            case BASIC: return new BigDecimal("3000.00");
            default: return new BigDecimal("1000.00");
        }
    }
}