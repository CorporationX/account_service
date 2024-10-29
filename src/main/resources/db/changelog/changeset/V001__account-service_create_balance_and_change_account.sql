CREATE TABLE balances
(
    id                    UUID PRIMARY KEY,
    authorization_balance DOUBLE PRECISION                      NOT NULL,
    actual_balance        DOUBLE PRECISION                      NOT NULL,
    created_at            timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at            timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version               BIGINT                                NOT NULL
);

ALTER TABLE payment_accounts
    ADD COLUMN balance_id UUID,
    ADD FOREIGN KEY (balance_id) REFERENCES balances (id);
