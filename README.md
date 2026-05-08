paywallet-lite-backend/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/
│   │   │   └── 📁 com/
│   │   │       └── 📁 paylogic/
│   │   │           └── 📁 paywalletlite/
│   │   │               │
│   │   │               ├── 📁 PayWalletLiteApplication.java          # Main class avec AnnotationConfigApplicationContext
│   │   │               │
│   │   │               ├── 📁 config/                                  # Configuration Spring Framework (XML + Java Config)
│   │   │               │   ├── 📁 root/
│   │   │               │   │   ├── RootConfig.java                     # @Configuration racine (services, repositories)
│   │   │               │   │   ├── AppConfig.java                      # Configuration applicative générale
│   │   │               │   │   └── PropertyConfig.java                 # Chargement properties
│   │   │               │   ├── 📁 web/
│   │   │               │   │   ├── WebConfig.java                      # @Configuration Web (controllers, view resolvers)
│   │   │               │   │   ├── WebAppInitializer.java                # WebApplicationInitializer (remplace web.xml)
│   │   │               │   │   ├── ServletConfig.java                    # DispatcherServlet configuration
│   │   │               │   │   └── CORSConfig.java                       # Configuration CORS
│   │   │               │   ├── 📁 security/
│   │   │               │   │   ├── SecurityConfig.java                   # Spring Security Configuration
│   │   │               │   │   ├── SecurityWebApplicationInitializer.java  # Initializer sécurité
│   │   │               │   │   ├── JwtAuthenticationFilter.java          # Filtre JWT personnalisé
│   │   │               │   │   ├── JwtTokenProvider.java                 # Génération/validation JWT
│   │   │               │   │   ├── JwtUserDetailsService.java            # UserDetailsService
│   │   │               │   │   ├── PasswordEncoderConfig.java            # BCryptPasswordEncoder
│   │   │               │   │   └── AccessDeniedHandlerImpl.java          # Gestion 403
│   │   │               │   ├── 📁 database/
│   │   │               │   │   ├── DataSourceConfig.java                 # Configuration Oracle DataSource
│   │   │               │   │   ├── JpaConfig.java                        # EntityManagerFactory, TransactionManager
│   │   │               │   │   ├── HibernateProperties.java              # Propriétés Hibernate
│   │   │               │   │   └── DatabaseMigrationConfig.java          # Flyway ou Liquibase
│   │   │               │   ├── 📁 kafka/
│   │   │               │   │   ├── KafkaConfig.java                      # ProducerFactory, ConsumerFactory
│   │   │               │   │   ├── KafkaProducerConfig.java                # Template Kafka
│   │   │               │   │   ├── KafkaConsumerConfig.java                # Listener Container Factory
│   │   │               │   │   └── KafkaTopicConfig.java                   # Création topics
│   │   │               │   ├── 📁 crypto/
│   │   │               │   │   ├── CryptoConfig.java                       # Beans cryptographiques
│   │   │               │   │   └── KeyManagementConfig.java                # Gestion clés serveur
│   │   │               │   └── 📁 scheduling/
│   │   │               │       ├── TaskSchedulerConfig.java                # ScheduledExecutorService
│   │   │               │       └── QuartzConfig.java                       # Jobs planifiés (token expiration, sync)
│   │   │               │
│   │   │               ├── 📁 domain/                                    # Entités JPA (même structure)
│   │   │               │   ├── 📁 identity/
│   │   │               │   │   ├── User.java
│   │   │               │   │   ├── Device.java
│   │   │               │   │   ├── KYCProfile.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── AccountStatus.java
│   │   │               │   │       ├── RoleType.java
│   │   │               │   │       ├── DeviceStatus.java
│   │   │               │   │       ├── DevicePlatform.java
│   │   │               │   │       └── KYCLevel.java
│   │   │               │   ├── 📁 wallet/
│   │   │               │   │   ├── Wallet.java
│   │   │               │   │   ├── WalletConfig.java
│   │   │               │   │   ├── WalletKeyPair.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── WalletType.java
│   │   │               │   │       ├── WalletStatus.java
│   │   │               │   │       ├── KeyStorageType.java
│   │   │               │   │       ├── KeyStatus.java
│   │   │               │   │       └── WalletConfigStatus.java
│   │   │               │   ├── 📁 crypto/
│   │   │               │   │   ├── Certificate.java
│   │   │               │   │   ├── CertificateAuthority.java
│   │   │               │   │   ├── RevocationList.java
│   │   │               │   │   ├── ServerKey.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── CertificateStatus.java
│   │   │               │   │       ├── CAStatus.java
│   │   │               │   │       ├── ServerKeyStatus.java
│   │   │               │   │       └── ServerKeyPurpose.java
│   │   │               │   ├── 📁 token/
│   │   │               │   │   ├── Token.java
│   │   │               │   │   ├── TokenSignature.java
│   │   │               │   │   ├── TokenTransferNode.java
│   │   │               │   │   ├── TokenDenomination.java
│   │   │               │   │   ├── TokenAllocationConfig.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── TokenStatus.java
│   │   │               │   │       ├── AllocationMode.java
│   │   │               │   │       ├── TransferStatus.java
│   │   │               │   │       └── TokenAllocationConfigStatus.java
│   │   │               │   ├── 📁 transaction/
│   │   │               │   │   ├── Transaction.java
│   │   │               │   │   ├── TransactionRefund.java
│   │   │               │   │   ├── TransactionMetadata.java
│   │   │               │   │   ├── Ledger.java
│   │   │               │   │   ├── LedgerEntry.java
│   │   │               │   │   ├── ReconciliationReport.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── TransactionType.java
│   │   │               │   │       ├── TransactionStatus.java
│   │   │               │   │       ├── OverpaymentStatus.java
│   │   │               │   │       ├── RefundStatus.java
│   │   │               │   │       ├── EntryType.java
│   │   │               │   │       ├── LedgerType.java
│   │   │               │   │       └── ReconciliationStatus.java
│   │   │               │   ├── 📁 credit/
│   │   │               │   │   ├── CreditConfig.java
│   │   │               │   │   ├── CreditLine.java
│   │   │               │   │   ├── CreditRepayment.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── CreditStatus.java
│   │   │               │   │       ├── CreditConfigStatus.java
│   │   │               │   │       ├── RepaymentType.java
│   │   │               │   │       └── RepaymentStatus.java
│   │   │               │   ├── 📁 risk/
│   │   │               │   │   ├── RiskProfile.java
│   │   │               │   │   ├── FraudAlert.java
│   │   │               │   │   └── enums/
│   │   │               │   │       ├── RiskLevel.java
│   │   │               │   │       ├── AlertType.java
│   │   │               │   │       ├── AlertSeverity.java
│   │   │               │   │       └── AlertStatus.java
│   │   │               │   └── 📁 notification/
│   │   │               │       ├── Notification.java
│   │   │               │       └── enums/
│   │   │               │           ├── NotificationType.java
│   │   │               │           ├── NotificationChannel.java
│   │   │               │           └── NotificationStatus.java
│   │   │               │
│   │   │               ├── 📁 repository/                                # Couche d'accès données
│   │   │               │   ├── 📁 identity/
│   │   │               │   │   ├── UserRepository.java                     # Interface
│   │   │               │   │   ├── UserRepositoryImpl.java                 # Implémentation JPA
│   │   │               │   │   ├── DeviceRepository.java
│   │   │               │   │   ├── DeviceRepositoryImpl.java
│   │   │               │   │   └── KYCProfileRepository.java
│   │   │               │   ├── 📁 wallet/
│   │   │               │   │   ├── WalletRepository.java
│   │   │               │   │   ├── WalletRepositoryImpl.java
│   │   │               │   │   ├── WalletConfigRepository.java
│   │   │               │   │   └── WalletConfigRepositoryImpl.java
│   │   │               │   ├── 📁 crypto/
│   │   │               │   │   ├── CertificateRepository.java
│   │   │               │   │   └── CertificateRepositoryImpl.java
│   │   │               │   ├── 📁 token/
│   │   │               │   │   ├── TokenRepository.java
│   │   │               │   │   ├── TokenRepositoryImpl.java
│   │   │               │   │   ├── TokenTransferNodeRepository.java
│   │   │               │   │   └── TokenAllocationConfigRepository.java
│   │   │               │   ├── 📁 transaction/
│   │   │               │   │   ├── TransactionRepository.java
│   │   │               │   │   ├── TransactionRepositoryImpl.java
│   │   │               │   │   ├── TransactionRefundRepository.java
│   │   │               │   │   ├── LedgerEntryRepository.java
│   │   │               │   │   └── ReconciliationReportRepository.java
│   │   │               │   ├── 📁 credit/
│   │   │               │   │   ├── CreditConfigRepository.java
│   │   │               │   │   ├── CreditConfigRepositoryImpl.java
│   │   │               │   │   ├── CreditLineRepository.java
│   │   │               │   │   └── CreditRepaymentRepository.java
│   │   │               │   ├── 📁 risk/
│   │   │               │   │   ├── RiskProfileRepository.java
│   │   │               │   │   └── FraudAlertRepository.java
│   │   │               │   └── 📁 notification/
│   │   │               │       └── NotificationRepository.java
│   │   │               │
│   │   │               ├── 📁 service/                                   # Couche métier
│   │   │               │   ├── 📁 identity/
│   │   │               │   │   ├── UserService.java                        # Interface
│   │   │               │   │   ├── UserServiceImpl.java                    # @Service
│   │   │               │   │   ├── AuthenticationService.java
│   │   │               │   │   ├── AuthenticationServiceImpl.java
│   │   │               │   │   ├── DeviceService.java
│   │   │               │   │   └── DeviceServiceImpl.java
│   │   │               │   ├── 📁 wallet/
│   │   │               │   │   ├── WalletService.java
│   │   │               │   │   ├── WalletServiceImpl.java
│   │   │               │   │   ├── WalletConfigService.java
│   │   │               │   │   └── WalletConfigServiceImpl.java
│   │   │               │   ├── 📁 token/
│   │   │               │   │   ├── TokenService.java
│   │   │               │   │   ├── TokenServiceImpl.java
│   │   │               │   │   ├── TokenAllocationService.java
│   │   │               │   │   ├── TokenAllocationServiceImpl.java
│   │   │               │   │   ├── TokenGenerationStrategy.java            # Interface stratégie
│   │   │               │   │   ├── AdaptiveDensityDistribution.java        # Implémentation algorithme
│   │   │               │   │   └── TokenSelectionOptimizer.java          # Algorithme sélection offline
│   │   │               │   ├── 📁 transaction/
│   │   │               │   │   ├── TransactionService.java
│   │   │               │   │   ├── TransactionServiceImpl.java
│   │   │               │   │   ├── LedgerService.java
│   │   │               │   │   ├── LedgerServiceImpl.java
│   │   │               │   │   ├── ReconciliationService.java
│   │   │               │   │   ├── ReconciliationServiceImpl.java
│   │   │               │   │   └── OverpaymentHandler.java
│   │   │               │   ├── 📁 credit/
│   │   │               │   │   ├── CreditService.java
│   │   │               │   │   ├── CreditServiceImpl.java
│   │   │               │   │   ├── CreditRiskEvaluator.java
│   │   │               │   │   └── CreditRepaymentScheduler.java         # @Scheduled ou Quartz
│   │   │               │   ├── 📁 sync/
│   │   │               │   │   ├── SynchronizationService.java
│   │   │               │   │   ├── SynchronizationServiceImpl.java
│   │   │               │   │   ├── SyncValidationService.java
│   │   │               │   │   └── TokenRedemptionService.java
│   │   │               │   ├── 📁 security/
│   │   │               │   │   ├── CryptographicService.java
│   │   │               │   │   ├── CryptographicServiceImpl.java
│   │   │               │   │   ├── CertificateService.java
│   │   │               │   │   ├── CertificateServiceImpl.java
│   │   │               │   │   ├── KeyRotationService.java
│   │   │               │   │   └── SignatureVerificationService.java
│   │   │               │   ├── 📁 risk/
│   │   │               │   │   ├── RiskAssessmentService.java
│   │   │               │   │   ├── RiskAssessmentServiceImpl.java
│   │   │               │   │   └── FraudDetectionEngine.java
│   │   │               │   ├── 📁 notification/
│   │   │               │   │   ├── NotificationService.java
│   │   │               │   │   ├── NotificationServiceImpl.java
│   │   │               │   │   ├── PushNotificationService.java
│   │   │               │   │   └── SmsNotificationService.java
│   │   │               │   └── 📁 audit/
│   │   │               │       ├── AuditService.java
│   │   │               │       ├── AuditServiceImpl.java
│   │   │               │       └── AuditLogChainVerifier.java
│   │   │               │
│   │   │               ├── 📁 controller/                                # Couche REST (Spring MVC)
│   │   │               │   ├── 📁 auth/
│   │   │               │   │   ├── AuthenticationController.java         # @Controller + @ResponseBody
│   │   │               │   │   └── DeviceController.java
│   │   │               │   ├── 📁 wallet/
│   │   │               │   │   └── WalletController.java
│   │   │               │   ├── 📁 token/
│   │   │               │   │   ├── TokenController.java
│   │   │               │   │   └── TokenAllocationController.java
│   │   │               │   ├── 📁 transaction/
│   │   │               │   │   └── TransactionController.java
│   │   │               │   ├── 📁 sync/
│   │   │               │   │   └── SynchronizationController.java
│   │   │               │   ├── 📁 credit/
│   │   │               │   │   └── CreditController.java
│   │   │               │   ├── 📁 admin/
│   │   │               │   │   ├── AdminWalletController.java
│   │   │               │   │   ├── AdminTransactionController.java
│   │   │               │   │   └── AdminRiskController.java
│   │   │               │   └── 📁 webhook/
│   │   │               │       └── WebhookController.java
│   │   │               │
│   │   │               ├── 📁 dto/                                       # Data Transfer Objects
│   │   │               │   ├── 📁 request/
│   │   │               │   │   ├── LoginRequestDto.java
│   │   │               │   │   ├── RegisterRequestDto.java
│   │   │               │   │   ├── TokenAllocationRequestDto.java
│   │   │               │   │   ├── OfflinePaymentRequestDto.java
│   │   │               │   │   ├── SyncRequestDto.java
│   │   │               │   │   └── CreditRequestDto.java
│   │   │               │   ├── 📁 response/
│   │   │               │   │   ├── AuthResponseDto.java
│   │   │               │   │   ├── WalletResponseDto.java
│   │   │               │   │   ├── TokenResponseDto.java
│   │   │               │   │   ├── TransactionResponseDto.java
│   │   │               │   │   ├── SyncResponseDto.java
│   │   │               │   │   └── ApiErrorResponseDto.java
│   │   │               │   └── 📁 kafka/
│   │   │               │       ├── TransactionEventDto.java
│   │   │               │       ├── TokenRedemptionEventDto.java
│   │   │               │       ├── FraudAlertEventDto.java
│   │   │               │       └── AuditEventDto.java
│   │   │               │
│   │   │               ├── 📁 mapper/                                  # MapStruct ou manuel
│   │   │               │   ├── UserMapper.java
│   │   │               │   ├── WalletMapper.java
│   │   │               │   ├── TokenMapper.java
│   │   │               │   └── TransactionMapper.java
│   │   │               │
│   │   │               ├── 📁 exception/                                 # Gestion erreurs
│   │   │               │   ├── GlobalExceptionHandler.java               # @ControllerAdvice
│   │   │               │   ├── BusinessException.java
│   │   │               │   ├── InsufficientFundsException.java
│   │   │               │   ├── TokenExpiredException.java
│   │   │               │   ├── DoubleSpendException.java
│   │   │               │   └── InvalidSignatureException.java
│   │   │               │
│   │   │               ├── 📁 kafka/                                     # Event-Driven Layer
│   │   │               │   ├── 📁 producer/
│   │   │               │   │   ├── TransactionEventProducer.java         # KafkaTemplate
│   │   │               │   │   ├── TokenRedemptionProducer.java
│   │   │               │   │   ├── FraudAlertProducer.java
│   │   │               │   │   ├── NotificationProducer.java
│   │   │               │   │   ├── AuditEventProducer.java
│   │   │               │   │   └── SynchronizationProducer.java
│   │   │               │   ├── 📁 consumer/
│   │   │               │   │   ├── TransactionEventConsumer.java           # @KafkaListener
│   │   │               │   │   ├── TokenRedemptionConsumer.java
│   │   │               │   │   ├── FraudAlertConsumer.java
│   │   │               │   │   ├── NotificationConsumer.java
│   │   │               │   │   ├── AuditEventConsumer.java
│   │   │               │   │   └── SynchronizationConsumer.java
│   │   │               │   ├── 📁 event/
│   │   │               │   │   ├── TransactionCreatedEvent.java
│   │   │               │   │   ├── TokenRedeemedEvent.java
│   │   │               │   │   ├── FraudDetectedEvent.java
│   │   │               │   │   ├── NotificationEvent.java
│   │   │               │   │   ├── AuditEvent.java
│   │   │               │   │   └── SyncCompletedEvent.java
│   │   │               │   └── 📁 handler/
│   │   │               │       ├── TransactionEventHandler.java
│   │   │               │       ├── FraudAlertHandler.java
│   │   │               │       └── NotificationDispatcher.java
│   │   │               │
│   │   │               ├── 📁 security/                                # Sécurité métier
│   │   │               │   ├── 📁 jwt/
│   │   │               │   │   ├── JwtTokenUtil.java
│   │   │               │   │   └── JwtAuthenticationEntryPoint.java
│   │   │               │   ├── 📁 crypto/
│   │   │               │   │   ├── EcdsaSignatureUtil.java
│   │   │               │   │   ├── AesEncryptionUtil.java
│   │   │               │   │   ├── KeyGeneratorUtil.java
│   │   │               │   │   └── HashUtil.java
│   │   │               │   └── 📁 certificate/
│   │   │               │       ├── CertificateValidator.java
│   │   │               │       ├── CertificateRevocationChecker.java
│   │   │               │       └── CertificateChainBuilder.java
│   │   │               │
│   │   │               ├── 📁 util/                                    # Utilitaires
│   │   │               │   ├── DateTimeUtil.java
│   │   │               │   ├── UuidGenerator.java
│   │   │               │   ├── MoneyUtil.java
│   │   │               │   └── ValidationUtil.java
│   │   │               │
│   │   │               └── 📁 validation/                              # Bean Validation
│   │   │                   ├── PhoneNumberValidator.java
│   │   │                   ├── TokenAmountValidator.java
│   │   │                   └── TransactionLimitValidator.java
│   │   │
│   │   └── 📁 resources/
│   │       ├── 📁 spring/
│   │       │   ├── root-context.xml                                    # Contexte racine (services, repos)
│   │       │   ├── servlet-context.xml                                 # Contexte web (controllers)
│   │       │   ├── security-context.xml                                # Spring Security config XML
│   │       │   ├── kafka-context.xml                                   # Kafka beans
│   │       │   ├── jpa-context.xml                                     # JPA/Hibernate beans
│   │       │   └── crypto-context.xml                                  # Beans cryptographiques
│   │       ├── 📁 db/
│   │       │   ├── 📁 migration/
│   │       │   │   ├── V1__init_schema.sql
│   │       │   │   ├── V2__add_token_system.sql
│   │       │   │   ├── V3__add_credit_system.sql
│   │       │   │   └── V4__add_audit_trail.sql
│   │       │   └── 📁 seed/
│   │       │       ├── denominations_seed.sql
│   │       │       └── wallet_configs_seed.sql
│   │       ├── 📁 kafka/
│   │       │   └── kafka-topics.json
│   │       ├── 📁 certs/
│   │       │   └── paylogic-ca.pem
│   │       ├── 📁 properties/
│   │       │   ├── application.properties                            # Propriétés communes
│   │       │   ├── application-dev.properties
│   │       │   ├── application-prod.properties
│   │       │   └── application-test.properties
│   │       ├── log4j2.xml                                            # Logging (pas logback)
│   │       └── 📁 messages/
│   │           ├── messages.properties
│   │           ├── messages_fr.properties
│   │           └── messages_en.properties
│   │
│   └── 📁 test/
│       ├── 📁 java/
│       │   └── 📁 com/paylogic/paywalletlite/
│       │       ├── 📁 unit/
│       │       │   ├── service/
│       │       │   ├── crypto/
│       │       │   └── util/
│       │       ├── 📁 integration/
│       │       │   ├── repository/
│       │       │   ├── kafka/
│       │       │   └── controller/
│       │       ├── 📁 e2e/
│       │       │   └── OfflinePaymentFlowTest.java
│       │       └── 📁 fixtures/
│       │           ├── TestDataFactory.java
│       │           └── MockTokenGenerator.java
│       └── 📁 resources/
│           ├── test-context.xml
│           ├── test-security-context.xml
│           └── 📁 sql/
│               ├── init-test-data.sql
│               └── cleanup.sql
│
├── 📁 webapp/                                                          # Ressources web (Spring MVC)
│   ├── 📁 WEB-INF/
│   │   └── web.xml                                                     # Fallback si pas d'initializer
│   └── 📁 static/
│       └── 📁 docs/
│           └── api-docs.html
│
├── 📁 docs/
│   ├── 📁 architecture/
│   │   ├── c4-model/
│   │   ├── sequence-diagrams/
│   │   └── data-flow/
│   ├── 📁 api/
│   │   ├── openapi.yml
│   │   └── postman-collection.json
│   ├── 📁 security/
│   │   ├── threat-model.md
│   │   └── security-checklist.md
│   └── 📁 deployment/
│       ├── deployment-guide.md
│       └── runbook.md
│
├── 📁 .github/
│   ├── 📁 workflows/
│   │   ├── ci-backend.yml
│   │   ├── cd-deploy.yml
│   │   └── security-scan.yml
│   └── 📁 pull_request_template.md
│
├── .gitignore
├── README.md
├── LICENSE
├── pom.xml                                                             # Maven parent
└── docker-compose.yml                                                  # Stack local (Oracle XE, Kafka, Zookeeper)
