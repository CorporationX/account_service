CREATE TABLE IF NOT EXISTS account
(
    id                  BIGSERIAL PRIMARY KEY,
    account_number      VARCHAR(255) NOT NULL UNIQUE,
    owner               VARCHAR(128) NOT NULL,
    account_type        VARCHAR(128) NOT NULL,
    currency            VARCHAR(128) NOT NULL,
    account_status      VARCHAR(128) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,
    account_version     BIGINT DEFAULT 0
 );
CREATE INDEX IF NOT EXISTS idx_account_owner ON account(owner);