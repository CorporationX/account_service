CREATE TABLE balance
(
    account_id         BIGINT         NOT NULL REFERENCES account (id) ON DELETE CASCADE PRIMARY KEY,
    authorized_balance NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    actual_balance     NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    balance_version    INT            NOT NULL DEFAULT 1,
    CONSTRAINT unique_account_id UNIQUE (account_id)
);

ALTER TABLE account
    ADD COLUMN balance_status VARCHAR(32) NOT NULL DEFAULT 'NEW';