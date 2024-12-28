CREATE TABLE balance_audit (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT NOT NULL,
    balance_version BIGINT NOT NULL,
    authorized_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    actual_balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    operation_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp,
    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account(id)
);
