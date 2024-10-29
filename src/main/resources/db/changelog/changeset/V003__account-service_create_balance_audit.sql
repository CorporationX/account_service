CREATE TABLE balance_audit
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number VARCHAR(20) NOT NULL,
    version BIGINT DEFAULT 0,
    authorization_balance DOUBLE PRECISION NOT NULL,
    actual_balance DOUBLE PRECISION NOT NULL,
    operation_id BIGINT,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,

    CONSTRAINT fk_account_number FOREIGN KEY (account_number) REFERENCES payment_accounts(account_number)
);

CREATE INDEX account_number_idx ON balance_audit (account_number);
