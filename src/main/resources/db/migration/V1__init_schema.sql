-- ============================================================
-- PAYWALLET LITE - SCHEMA INITIAL
-- ============================================================

-- Users
CREATE TABLE pwl_app.users (
    user_id RAW(16) DEFAULT SYS_GUID() PRIMARY KEY,
    role VARCHAR2(20) NOT NULL CHECK (role IN ('ADMIN','CUSTOMER','MERCHANT','AGENT')),
    first_name VARCHAR2(100) NOT NULL,
    last_name VARCHAR2(100) NOT NULL,
    phone_number VARCHAR2(20) NOT NULL UNIQUE,
    email VARCHAR2(255),
    national_id_number VARCHAR2(50),
    password_hash VARCHAR2(255) NOT NULL,
    pin_hash VARCHAR2(255) NOT NULL,
    registration_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR2(20) NOT NULL DEFAULT 'PENDING_VERIFICATION' CHECK (status IN ('ACTIVE','SUSPENDED','CLOSED','PENDING_VERIFICATION')),
    last_login TIMESTAMP,
    failed_login_attempts NUMBER(10) DEFAULT 0,
    locked_until TIMESTAMP,
    kyc_verification_status VARCHAR2(50)
);

CREATE INDEX idx_users_phone ON pwl_app.users(phone_number);
CREATE INDEX idx_users_status ON pwl_app.users(status);

-- Wallets (à compléter)
-- CREATE TABLE pwl_app.wallets (...);

COMMIT;