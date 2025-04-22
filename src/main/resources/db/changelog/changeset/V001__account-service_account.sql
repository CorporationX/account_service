CREATE TABLE IF NOT EXISTS payment_account
(
    account_number VARCHAR(20) PRIMARY KEY,
    owner_id       BIGINT,
    owner_type     VARCHAR(32),
    description    VARCHAR(512),
    status         VARCHAR(32),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at      TIMESTAMP,
    currency       VARCHAR(32),
    account_type   VARCHAR(32),
    version        BIGINT
);

CREATE INDEX IF NOT EXISTS idx_payment_account_owner ON payment_account (owner_id);