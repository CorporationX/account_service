CREATE TABLE payment_owners (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL
);

CREATE TABLE payment_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number VARCHAR(20) NOT NULL UNIQUE,
    owner_id UUID NOT NULL,
    type VARCHAR(32) NOT NULL,
    currency VARCHAR(8) NOT NULL,
    status VARCHAR(32) DEFAULT 'ACTIVE' NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp NOT NULL,
    closed_at timestamptz,
    version BIGINT DEFAULT 0 NOT NULL,

    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES payment_owners(id)
);

CREATE INDEX idx_payment_owners_external_type ON payment_owners (external_id, type);
CREATE INDEX idx_account_number ON payment_accounts(account_number);