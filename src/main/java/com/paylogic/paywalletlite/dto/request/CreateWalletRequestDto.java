package com.paylogic.paywalletlite.dto.request;

import com.paylogic.paywalletlite.domain.wallet.enums.CurrencyCode;
import com.paylogic.paywalletlite.domain.wallet.enums.WalletType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
}