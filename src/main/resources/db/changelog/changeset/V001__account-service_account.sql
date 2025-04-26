CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE CHECK (char_length(account_number) >= 12),
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(10) NOT NULL CHECK (owner_type IN ('USER', 'PROJECT')),
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('PERSONAL', 'BUSINESS')),
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('RUB', 'USD', 'EUR')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    closed_at TIMESTAMP,
    version INT NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_account_owner_id ON account (owner_id);
CREATE INDEX IF NOT EXISTS idx_account_number ON account (account_number);

