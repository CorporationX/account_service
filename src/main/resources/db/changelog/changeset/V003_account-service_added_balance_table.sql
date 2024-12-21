CREATE TABLE balance
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id         BIGINT         NOT NULL REFERENCES account (id) ON DELETE RESTRICT,
    authorized_balance NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    actual_balance     NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    balance_version    INT            NOT NULL DEFAULT 1
);