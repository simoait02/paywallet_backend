package com.paylogic.paywalletlite.domain.crypto;

import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "server_keys", schema = "pwl_app")
public class ServerKey {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "server_key_id", updatable = false, nullable = false)
    private UUID serverKeyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "key_purpose", nullable = false, length = 30)
    private ServerKeyPurpose keyPurpose;

    @Column(name = "public_key_pem", nullable = false, length = 4000)
    private String publicKeyPem;

    @Column(name = "private_key_encrypted", nullable = false, length = 4000)
    private String privateKeyEncrypted;

    @Column(name = "key_algorithm", length = 50)
    private String keyAlgorithm;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "rotated_at")
    private LocalDateTime rotatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ServerKeyStatus status;

    @Column(name = "kms_reference", length = 255)
    private String kmsReference;

    // Relation vers le wallet qui utilise cette clé (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @Column(name = "wallet_id", insertable = false, updatable = false)
    private UUID walletId;

    public ServerKey() {
        this.createdAt = LocalDateTime.now();
        this.status = ServerKeyStatus.ACTIVE;
    }

    // Getters & Setters
    public UUID getServerKeyId() { return serverKeyId; }
    public void setServerKeyId(UUID serverKeyId) { this.serverKeyId = serverKeyId; }

    public ServerKeyPurpose getKeyPurpose() { return keyPurpose; }
    public void setKeyPurpose(ServerKeyPurpose keyPurpose) { this.keyPurpose = keyPurpose; }

    public String getPublicKeyPem() { return publicKeyPem; }
    public void setPublicKeyPem(String publicKeyPem) { this.publicKeyPem = publicKeyPem; }

    public String getPrivateKeyEncrypted() { return privateKeyEncrypted; }
    public void setPrivateKeyEncrypted(String privateKeyEncrypted) { this.privateKeyEncrypted = privateKeyEncrypted; }

    public String getKeyAlgorithm() { return keyAlgorithm; }
    public void setKeyAlgorithm(String keyAlgorithm) { this.keyAlgorithm = keyAlgorithm; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getRotatedAt() { return rotatedAt; }
    public void setRotatedAt(LocalDateTime rotatedAt) { this.rotatedAt = rotatedAt; }

    public ServerKeyStatus getStatus() { return status; }
    public void setStatus(ServerKeyStatus status) { this.status = status; }

    public String getKmsReference() { return kmsReference; }
    public void setKmsReference(String kmsReference) { this.kmsReference = kmsReference; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
        if (wallet != null) {
            this.walletId = wallet.getWalletId();
        }
    }

    public UUID getWalletId() {
        return walletId != null ? walletId :
                (wallet != null ? wallet.getWalletId() : null);
    }

    // Méthodes métier
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isActive() {
        return status == ServerKeyStatus.ACTIVE && !isExpired();
    }
}