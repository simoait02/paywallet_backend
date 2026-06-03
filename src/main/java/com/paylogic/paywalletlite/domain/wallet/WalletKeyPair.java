package com.paylogic.paywalletlite.domain.wallet;

import com.paylogic.paywalletlite.domain.wallet.enums.KeyStatus;
import com.paylogic.paywalletlite.domain.wallet.enums.KeyStorageType;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wallet_key_pairs", schema = "pwl_app")
public class WalletKeyPair {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "keypair_id", updatable = false, nullable = false)
    private UUID keypairId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(name = "wallet_id", insertable = false, updatable = false)
    private UUID walletId;

    @Column(name = "public_key", nullable = false, length = 4000)
    private String publicKey;

    @Column(name = "private_key_encrypted", nullable = false, length = 4000)
    private String privateKeyEncrypted;

    @Column(name = "key_algorithm", length = 50)
    private String keyAlgorithm;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false, length = 30)
    private KeyStorageType storageType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "rotated_at")
    private LocalDateTime rotatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private KeyStatus status;

    @Column(name = "rotation_reason", length = 255)
    private String rotationReason;

    @Column(name = "server_issuance_signature", length = 4000)
    private String serverIssuanceSignature;

    public WalletKeyPair() {
        this.createdAt = LocalDateTime.now();
        this.status = KeyStatus.ACTIVE;
    }

    // Getters et Setters
    public UUID getKeypairId() { return keypairId; }
    public void setKeypairId(UUID keypairId) { this.keypairId = keypairId; }

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPrivateKeyEncrypted() { return privateKeyEncrypted; }
    public void setPrivateKeyEncrypted(String privateKeyEncrypted) { this.privateKeyEncrypted = privateKeyEncrypted; }

    public String getKeyAlgorithm() { return keyAlgorithm; }
    public void setKeyAlgorithm(String keyAlgorithm) { this.keyAlgorithm = keyAlgorithm; }

    public KeyStorageType getStorageType() { return storageType; }
    public void setStorageType(KeyStorageType storageType) { this.storageType = storageType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getRotatedAt() { return rotatedAt; }
    public void setRotatedAt(LocalDateTime rotatedAt) { this.rotatedAt = rotatedAt; }

    public KeyStatus getStatus() { return status; }
    public void setStatus(KeyStatus status) { this.status = status; }

    public String getRotationReason() { return rotationReason; }
    public void setRotationReason(String rotationReason) { this.rotationReason = rotationReason; }

    public String getServerIssuanceSignature() { return serverIssuanceSignature; }
    public void setServerIssuanceSignature(String serverIssuanceSignature) { this.serverIssuanceSignature = serverIssuanceSignature; }
}