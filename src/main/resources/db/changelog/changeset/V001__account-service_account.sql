CREATE TABLE IF NOT EXISTS account
(
    id                  BIGSERIAL PRIMARY KEY,
    account_number      VARCHAR(32) NOT NULL UNIQUE,
    account_type        VARCHAR(16) NOT NULL CHECK (account_type IN ('SAVINGS', 'CHECKING', 'CREDIT', 'DEPOSIT')),
    currency            VARCHAR(16) NOT NULL DEFAULT 'RUB',
    balance             DECIMAL(19,2) DEFAULT 0.00,
    account_status      VARCHAR(16) NOT NULL CHECK (account_status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,
    account_version     BIGINT DEFAULT 1


 );

CREATE INDEX IF NOT EXISTS idx_account_number ON account(account_number);

CREATE TABLE IF NOT EXISTS owner
(
    id                  BIGSERIAL PRIMARY KEY,
    person_id           BIGINT NOT NULL,
    owner_type          VARCHAR(32) NOT NULL CHECK (owner_type IN ('USER', 'PROJECT')),
    account_id          BIGINT NOT NULL,

CONSTRAINT fk_owner_account FOREIGN KEY (account_id) REFERENCES account(id)
);

