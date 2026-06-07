-- V5: Device-managed wallet keys
--
-- The wallet ECDSA privkey moves from server-encrypted storage to the device's
-- secure element (Android Keystore / iOS Secure Enclave). The CSR-based flow
-- means the backend now only ever sees the public key.
--
-- For new wallets, wallet_key_pairs.private_key_encrypted is NULL and
-- storage_type is 'DEVICE_ONLY'. Existing rows keep their previous values.

ALTER TABLE pwl_app.wallet_key_pairs
    MODIFY (private_key_encrypted VARCHAR2(4000) NULL);
