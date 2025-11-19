CREATE TABLE IF NOT EXISTS account
(
    id                  BIGSERIAL PRIMARY KEY,
    account_number      VARCHAR(20) NOT NULL UNIQUE,
    account_type        VARCHAR(16) NOT NULL,
    currency            VARCHAR(16) NOT NULL DEFAULT 'RUB',
    balance             DECIMAL(19,2) DEFAULT 0.00,
    account_status      VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,
    account_version     BIGINT DEFAULT 0


 );

CREATE TABLE IF NOT EXISTS owner
(
    id                  BIGSERIAL PRIMARY KEY,
    person_id           BIGINT NOT NULL,
    owner_person        VARCHAR(32) NOT NULL,
    account_id          BIGINT NOT NULL,

CONSTRAINT fk_owner_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE INDEX IF NOT EXISTS idx_account_number ON account(account_number);