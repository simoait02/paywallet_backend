package com.paylogic.paywalletlite.service.security;

import com.paylogic.paywalletlite.domain.crypto.Certificate;
import com.paylogic.paywalletlite.domain.crypto.CertificateAuthority;
import com.paylogic.paywalletlite.domain.crypto.RevocationList;
import com.paylogic.paywalletlite.domain.crypto.enums.CAStatus;
import com.paylogic.paywalletlite.domain.crypto.enums.CertificateStatus;
import com.paylogic.paywalletlite.domain.wallet.Wallet;
import com.paylogic.paywalletlite.exception.BusinessException;
import com.paylogic.paywalletlite.repository.crypto.CertificateAuthorityRepository;
import com.paylogic.paywalletlite.repository.crypto.CertificateRepository;
import com.paylogic.paywalletlite.repository.crypto.RevocationListRepository;
import com.paylogic.paywalletlite.repository.wallet.WalletRepository;
import com.paylogic.paywalletlite.security.crypto.HashUtil;
import com.paylogic.paywalletlite.security.crypto.KeyGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CertificateServiceImpl implements CertificateService {

    private static final int CERT_LIFETIME_DAYS = 365;

    private final CertificateRepository certificateRepository;
    private final CertificateAuthorityRepository caRepository;
    private final RevocationListRepository revocationListRepository;
    private final WalletRepository walletRepository;
    private final KeyGeneratorUtil keyGeneratorUtil;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository,
                                  CertificateAuthorityRepository caRepository,
                                  RevocationListRepository revocationListRepository,
                                  WalletRepository walletRepository,
                                  KeyGeneratorUtil keyGeneratorUtil) {
        this.certificateRepository = certificateRepository;
        this.caRepository = caRepository;
        this.revocationListRepository = revocationListRepository;
        this.walletRepository = walletRepository;
        this.keyGeneratorUtil = keyGeneratorUtil;
    }

    @Override
    @Transactional
    public Certificate issueCertificate(UUID walletId, UUID caId) throws BusinessException {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found: " + walletId));

        CertificateAuthority ca = caRepository.findById(caId)
                .orElseThrow(() -> new BusinessException("CA not found: " + caId));

        if (!ca.isActive()) {
            throw new BusinessException("CA is not active: " + caId);
        }

        // Générer une paire de clés pour le wallet
        KeyGeneratorUtil.KeyPairEncoded keyPair = keyGeneratorUtil.generateEncodedKeyPair();

        // Créer le certificat
        Certificate certificate = new Certificate();
        certificate.setWallet(wallet);
        certificate.setCertificatePem(buildCertificatePem(wallet, keyPair, ca));
        certificate.setThumbprint(generateThumbprint(certificate.getCertificatePem()));
        certificate.setIssuerCa(ca);
        certificate.setIssuedAt(LocalDateTime.now());
        certificate.setExpiresAt(LocalDateTime.now().plusDays(CERT_LIFETIME_DAYS));
        certificate.setStatus(CertificateStatus.VALID);

        // Mettre à jour le wallet avec la clé publique
        wallet.setPublicKey(keyPair.getPublicKeyBase64());

        certificateRepository.save(certificate);
        walletRepository.save(wallet);

        return certificate;
    }

    @Override
    public boolean validateCertificate(UUID certificateId) throws BusinessException {
        Certificate cert = findById(certificateId);

        if (cert.getStatus() != CertificateStatus.VALID) {
            return false;
        }
        if (cert.isExpired()) {
            return false;
        }
        if (revocationListRepository.existsByCertificateId(certificateId)) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void revokeCertificate(UUID certificateId, String reason) throws BusinessException {
        Certificate cert = findById(certificateId);

        if (cert.getStatus() == CertificateStatus.REVOKED) {
            throw new BusinessException("Certificate already revoked: " + certificateId);
        }

        cert.revoke(reason);
        certificateRepository.save(cert);

        // Ajouter à la liste de révocation
        RevocationList entry = new RevocationList();
        entry.setCertificate(cert);
        entry.setReason(reason);
        entry.setRevokedBy("SYSTEM");
        entry.setCrlEntrySerial(cert.getThumbprint());
        revocationListRepository.save(entry);
    }

    @Override
    public Certificate findById(UUID certificateId) throws BusinessException {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new BusinessException("Certificate not found: " + certificateId));
    }

    @Override
    public List<Certificate> findByWalletId(UUID walletId) {
        return certificateRepository.findByWalletId(walletId);
    }

    @Override
    public CertificateAuthority getActiveCA() throws BusinessException {
        return caRepository.findActive()
                .orElseThrow(() -> new BusinessException("No active CA found"));
    }

    @Override
    @Transactional
    public CertificateAuthority initializeRootCA(String caName) throws BusinessException {
        if (caRepository.existsByCaName(caName)) {
            throw new BusinessException("CA already exists with name: " + caName);
        }

        KeyGeneratorUtil.KeyPairEncoded keyPair = keyGeneratorUtil.generateEncodedKeyPair();

        CertificateAuthority ca = new CertificateAuthority();
        ca.setCaName(caName);
        ca.setPublicKeyPem(keyPair.getPublicKeyBase64());
        ca.setCaCertificatePem(buildSelfSignedCaPem(caName, keyPair));
        ca.setCreatedAt(LocalDateTime.now());
        ca.setExpiresAt(LocalDateTime.now().plusYears(10));
        ca.setKeyAlgorithm("ECDSA_P256");
        ca.setStatus(CAStatus.ACTIVE);

        return caRepository.save(ca);
    }

    // ============================================================
    // UTILITAIRES
    // ============================================================

    private String generateThumbprint(String certificatePem) {
        if (certificatePem == null || certificatePem.isEmpty()) {
            throw new BusinessException("Cannot generate thumbprint: certificate PEM is empty");
        }

        String hash = HashUtil.sha256(certificatePem);

        if (hash == null || hash.length() < 64) {
            // Si le hash est plus court, retournez-le tel quel ou paddé
            return hash != null ? hash : "";
        }

        return hash.substring(0, 64);
    }
    private String buildCertificatePem(Wallet wallet, KeyGeneratorUtil.KeyPairEncoded keyPair, CertificateAuthority ca) {
        return "-----BEGIN CERTIFICATE-----\n" +
                "WalletID: " + wallet.getWalletId() + "\n" +
                "PublicKey: " + keyPair.getPublicKeyBase64() + "\n" +
                "Issuer: " + ca.getCaName() + "\n" +
                "-----END CERTIFICATE-----";
    }

    private String buildSelfSignedCaPem(String caName, KeyGeneratorUtil.KeyPairEncoded keyPair) {
        return "-----BEGIN CA CERTIFICATE-----\n" +
                "CAName: " + caName + "\n" +
                "PublicKey: " + keyPair.getPublicKeyBase64() + "\n" +
                "SelfSigned: true\n" +
                "-----END CA CERTIFICATE-----";
    }
}