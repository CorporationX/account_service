CREATE TABLE free_account_numbers(
    type VARCHAR(16) NOT NULL,
    number BIGINT NOT NULL,

    CONSTRAINT free_account_numbers_pk PRIMARY KEY (type, number)
);

CREATE TABLE account_numbers_sequence(
    type VARCHAR(16) NOT NULL PRIMARY KEY,
    count BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL CHECK (account_number ~ '^[0-9]{12,20}$'),
    owner_type VARCHAR(10) NOT NULL CHECK (owner_type IN ('USER', 'PROJECT')),
    owner_id BIGINT NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    currency VARCHAR(3) NOT NULL CHECK (currency IN ('RUB', 'EUR', 'USD')),
    account_status VARCHAR(20) NOT NULL CHECK (account_status IN ('ACTIVE', 'BLOCKED', 'CLOSED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP WITH TIME ZONE,
    version INTEGER NOT NULL DEFAULT 0,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0,
    CONSTRAINT unique_account_number UNIQUE (account_number)
);

CREATE INDEX idx_account_owner_type_owner_id ON account (owner_type, owner_id);
CREATE INDEX idx_account_status ON account (account_status);