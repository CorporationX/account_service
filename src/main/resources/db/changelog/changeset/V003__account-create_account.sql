CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance NUMERIC(20, 2) NOT NULL DEFAULT 0.00,
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(20) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,

     CONSTRAINT chk_account_number_format CHECK (account_number ~ '^\d{12,20}$'),
     CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

CREATE INDEX idx_account_owner_id_owner_type ON account (owner_id, owner_type);

