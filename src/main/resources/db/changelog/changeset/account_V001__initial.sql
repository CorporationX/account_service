CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(14) NOT NULL UNIQUE CHECK (char_length(account_number) = 14),
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(16) NOT NULL CHECK (owner_type IN ('USER', 'PROJECT')),
    account_type VARCHAR(16) NOT NULL CHECK (account_type IN ('PERSONAL', 'BUSINESS')),
    currency VARCHAR(4) NOT NULL CHECK (currency IN ('RUB', 'USD', 'EUR')),
    status VARCHAR(16) NOT NULL CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_account_owner_id ON account (owner_id);

CREATE SEQUENCE account_number_seq
  START WITH 1
  INCREMENT BY 1
  MINVALUE 1
  MAXVALUE 999999
  CYCLE;