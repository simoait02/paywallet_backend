package com.paylogic.paywalletlite.dto.request;

public class SignatureVerifyRequestDto {

    private String data;
    private String signature;
    private String publicKey;

    public SignatureVerifyRequestDto() {}

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}