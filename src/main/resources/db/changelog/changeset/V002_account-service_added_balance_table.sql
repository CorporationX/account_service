CREATE TABLE IF NOT EXISTS balance
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id         BIGINT         NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    authorized_balance NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    actual_balance     NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    balance_version    INT            NOT NULL DEFAULT 1
);