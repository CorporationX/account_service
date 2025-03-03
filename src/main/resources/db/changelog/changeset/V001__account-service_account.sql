CREATE TABLE account
(
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(255) NOT NULL UNIQUE,
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    currency_code VARCHAR(4) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version INT NOT NULL
);

CREATE INDEX idx_account_owner ON account (owner_id, owner_type);