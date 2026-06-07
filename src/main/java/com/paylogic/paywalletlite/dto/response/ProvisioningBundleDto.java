package com.paylogic.paywalletlite.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Everything the device needs to operate offline:
 * <ul>
 *   <li>{@code walletCertificatePem}: CA-signed certificate binding the wallet to
 *       its device pubkey. Presented to peers during the NFC/BLE handshake.</li>
 *   <li>{@code caPublicKeyPem}: trust anchor. The device caches this and uses it
 *       to verify the peer's certificate during offline transfers.</li>
 *   <li>{@code trustedServerKeys}: list of currently-trusted server signing keys
 *       (active + recently rotated) used to verify {@code TokenSignature} blobs.</li>
 * </ul>
 *
 * The device should refresh this bundle periodically (e.g., on every successful
 * sync) so it picks up CA / server-key rotations.
 */
public class ProvisioningBundleDto {

    private UUID walletId;
    private String walletCertificatePem;
    private String caPublicKeyPem;
    private List<TrustedServerKeyDto> trustedServerKeys;

    public ProvisioningBundleDto() {}

    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public String getWalletCertificatePem() { return walletCertificatePem; }
    public void setWalletCertificatePem(String walletCertificatePem) {
        this.walletCertificatePem = walletCertificatePem;
    }

    public String getCaPublicKeyPem() { return caPublicKeyPem; }
    public void setCaPublicKeyPem(String caPublicKeyPem) { this.caPublicKeyPem = caPublicKeyPem; }

    public List<TrustedServerKeyDto> getTrustedServerKeys() { return trustedServerKeys; }
    public void setTrustedServerKeys(List<TrustedServerKeyDto> trustedServerKeys) {
        this.trustedServerKeys = trustedServerKeys;
    }
}
