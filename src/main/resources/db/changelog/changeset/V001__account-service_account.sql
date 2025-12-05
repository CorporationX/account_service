CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(20) NOT NULL CHECK (owner_type IN ('USER', 'PROJECT')),
    type VARCHAR(50) NOT NULL CHECK (type IN ('INDIVIDUAL_CHECKING', 'LEGAL_ENTITY_CHECKING', 'CURRENCY_ACCOUNT', 'SAVINGS_ACCOUNT', 'CURRENT_ACCOUNT')),
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('RUB', 'USD', 'EUR')),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT account_number_length CHECK (LENGTH(number) >= 12 AND LENGTH(number) <= 20)
);

CREATE INDEX idx_account_owner ON account(owner_id, owner_type);
CREATE INDEX idx_account_status ON account(status);
CREATE INDEX idx_account_currency ON account(currency);
CREATE INDEX idx_account_number ON account(number);
