package com.paylogic.paywalletlite.dto.request;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour la demande de synchronisation différée.
 *
 * Le device du receiver envoie un lot de transactions offline
 * contenant les tokens avec leur chaîne de transfert complète.
 *
 * Format aligné avec :
 * ┌─────────────────────────────────────────────────────────────┐
 * │                      SyncBatch #BATCH-042                     │
 * │  Statut: COMPLETED | Transactions: 4 | Écart: 0 XAF          │
 * ├─────────────────────────────────────────────────────────────┤
 * │  Transaction #TX-101                                          │
 * │  ├── Type: TOKEN_REDEMPTION                                   │
 * │  ├── Token: TKN-001 (valeur: 500 XAF)                         │
 * │  ├── Sender: WAL-ALICE → Receiver: WAL-BOB                    │
 * │  └── Preuve: chaîne de transfert                              │
 * └─────────────────────────────────────────────────────────────┘
 */
public class SyncRequestDto {

    @NotNull(message = "Wallet ID is required")
    private UUID walletId;

    @NotNull(message = "Device ID is required")
    private UUID deviceId;

    /**
     * Liste des transactions offline à synchroniser.
     * Chaque transaction contient les tokens avec leur chaîne de transfert.
     */
    @NotEmpty(message = "At least one transaction is required")
    @Valid
    private List<OfflineTransactionDto> transactions;

    /** Signature du device pour authentification de la demande */
    private String deviceSignature;

    /** Timestamp du device au moment de l'envoi (pour vérification anti-rejeu) */
    private String deviceTimestamp;

    public SyncRequestDto() {}

    // Getters et Setters
    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }

    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }

    public List<OfflineTransactionDto> getTransactions() { return transactions; }
    public void setTransactions(List<OfflineTransactionDto> transactions) { this.transactions = transactions; }

    public String getDeviceSignature() { return deviceSignature; }
    public void setDeviceSignature(String deviceSignature) { this.deviceSignature = deviceSignature; }

    public String getDeviceTimestamp() { return deviceTimestamp; }
    public void setDeviceTimestamp(String deviceTimestamp) { this.deviceTimestamp = deviceTimestamp; }
}