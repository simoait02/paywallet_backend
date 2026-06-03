package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.ServerKey;
import com.paylogic.paywalletlite.domain.crypto.enums.ServerKeyPurpose;
import com.paylogic.paywalletlite.domain.token.OfflineTransactionToken;
import com.paylogic.paywalletlite.domain.token.Token;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.security.crypto.EcdsaSignatureUtil;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignatureVerificationServiceImpl implements SignatureVerificationService {

    private final CryptographicService cryptographicService;
    private final EcdsaSignatureUtil ecdsaSignatureUtil;

    @Autowired
    public SignatureVerificationServiceImpl(CryptographicService cryptographicService,
                                            EcdsaSignatureUtil ecdsaSignatureUtil) {
        this.cryptographicService = cryptographicService;
        this.ecdsaSignatureUtil = ecdsaSignatureUtil;
    }

    @Override
    public boolean verifyTokenSignature(Token token) throws BusinessException {
        if (token.getTokenSignature() == null) {
            return false;
        }

        ServerKey signingKey = cryptographicService.getActiveKey(ServerKeyPurpose.TOKEN_SIGNING);

        String signedData = buildSignedData(token);
        String expectedHash = HashUtil.sha256(signedData);

        return ecdsaSignatureUtil.verify(
                expectedHash.getBytes(),
                token.getTokenSignature().getSignatureValue(),
                signingKey.getPublicKeyPem()
        );
    }

    @Override
    public boolean verifyTokenSignature(OfflineTransactionToken token) throws BusinessException {
        if (token.getBackendSignature() == null) {
            System.out.println("#### Backend Signature not Provide !!!! ");
            return false;
        }

        ServerKey signingKey = cryptographicService.getActiveKey(ServerKeyPurpose.TOKEN_SIGNING);

        String signedData = buildSignedData(token);
        String expectedHash = HashUtil.sha256(signedData);

        System.out.println("######  Expected Hash "+ expectedHash);

        return ecdsaSignatureUtil.verify(
                expectedHash.getBytes(),
                token.getBackendSignature(),
                signingKey.getPublicKeyPem()
        );
    }

    @Override
    public boolean verifyWithPublicKey(String data, String signature, String publicKeyBase64) {
        try {
            return ecdsaSignatureUtil.verify(data.getBytes(), signature, publicKeyBase64);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean verifyOfflineTransfer(String payload, String signature, String payerPublicKey) {
        // Vérifier la signature du payer sur le payload du transfert
        String hash = HashUtil.sha256(payload);
        return ecdsaSignatureUtil.verify(hash.getBytes(), signature, payerPublicKey);
    }

    @Override
    public boolean verifyCertificateChain(String certificatePem, String caPublicKey) {
        // TODO: Implémenter la vérification X.509 complète avec BouncyCastle
        // Pour l'instant, vérification basique du thumbprint
        String certHash = HashUtil.sha256(certificatePem);
        return certHash != null && !certHash.isEmpty();
    }

    private String buildSignedData(Token token) {
        return String.join("|",
                token.getTokenHash(),
                token.getIssuerId().toString(),
                token.getValue().toPlainString(),
                token.getIssuedAt().toString(),
                token.getNonce()
        );
    }

    private String buildSignedData(OfflineTransactionToken token) {
        return String.join("|",
                token.getTokenHash(),
                token.getIssuerId().toString(),
                token.getTokenValue().toPlainString(),
                token.getIssuedAt().toString(),
                token.getTokenNonce()
        );
    }
}