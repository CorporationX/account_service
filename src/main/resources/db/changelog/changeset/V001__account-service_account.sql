CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    owner_type VARCHAR(255) NOT NULL,
    owner_id bigint NOT NULL,
    type VARCHAR(255) NOT NULL,
    currency VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version bigint DEFAULT 1
);

CREATE INDEX idx_account_owner_id ON account (owner_id);
