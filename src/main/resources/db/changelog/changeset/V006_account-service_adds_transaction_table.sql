CREATE TABLE transaction
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    transaction_type VARCHAR(32) NOT NULL,
    transaction_amount NUMERIC(18, 2) NOT NULL,
    transaction_status VARCHAR(32) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at timestamptz  DEFAULT NULL,
    transaction_version INT NOT NULL DEFAULT 1
);

CREATE INDEX account_id_idx ON transaction (account_id);