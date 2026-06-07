package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.wallet.enums.CurrencyCode;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class CreateWalletRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private UUID configId;

    @NotNull(message = "Wallet type is required")
    private WalletType walletType;

    @NotNull(message = "Wallet currency is required")
    private CurrencyCode walletCurrency;

    @NotNull(message = "User ID is required")
    private UUID userId;

    // Optional: custom config override (null = use defaults)
    private String configName;

    /**
     * Device-generated ECDSA P-256 public key (X.509 SubjectPublicKeyInfo, base64).
     * The corresponding private key never leaves the device's secure element.
     */
    @NotBlank(message = "Device public key is required")
    private String publicKeyBase64;

    /**
     * Proof of possession: ECDSA-SHA256 signature over
     * "userId|publicKeyBase64|popTimestamp", produced by the device's secure-enclave
     * privkey. Base64-encoded.
     */
    @NotBlank(message = "Proof of possession is required")
    private String proofOfPossession;

    /**
     * Epoch milliseconds at which the PoP signature was produced. Must be within
     * a freshness window (±5 minutes) of server time.
     */
    @NotNull(message = "PoP timestamp is required")
    private Long popTimestamp;

    /**
     * Device key-attestation chain (Android: Keystore cert chain rooted at Google
     * attestation root; iOS: empty + a separate App Attest assertion). Optional
     * for v1 — backend logs presence/absence; future B1.5 will enforce verification.
     */
    private List<String> attestationChain;

    public WalletType getWalletType() {
        return walletType;
    }

    public void setWalletType(WalletType walletType) {
        this.walletType = walletType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public UUID getConfigId() { return configId; }
    public void setConfigId(UUID configId) { this.configId = configId;}

    public CurrencyCode getWalletCurrency() {
        return walletCurrency;
    }

    public void setWalletCuurency(CurrencyCode walletCurrency) {
        this.walletCurrency = walletCurrency;
    }

    public String getPublicKeyBase64() { return publicKeyBase64; }
    public void setPublicKeyBase64(String publicKeyBase64) { this.publicKeyBase64 = publicKeyBase64; }

    public String getProofOfPossession() { return proofOfPossession; }
    public void setProofOfPossession(String proofOfPossession) { this.proofOfPossession = proofOfPossession; }

    public Long getPopTimestamp() { return popTimestamp; }
    public void setPopTimestamp(Long popTimestamp) { this.popTimestamp = popTimestamp; }

    public List<String> getAttestationChain() { return attestationChain; }
    public void setAttestationChain(List<String> attestationChain) { this.attestationChain = attestationChain; }
}