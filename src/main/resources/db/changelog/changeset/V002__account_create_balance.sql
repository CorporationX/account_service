CREATE TABLE IF NOT EXISTS balance
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id     BIGINT         NOT NULL REFERENCES accounts (id),
    auth_balance   NUMERIC(15, 2) NOT NULL,
    actual_balance NUMERIC(15, 2) NOT NULL,
    created_at     timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamptz DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT      DEFAULT 0
);