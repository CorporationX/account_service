CREATE TABLE IF NOT EXISTS balance(
    balance_id BIGINT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    authorization_balance NUMERIC(15, 2) NOT NULL,
    factual_balance NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,
    CONSTRAINT fk_balance_account
        FOREIGN KEY (account_number)
        REFERENCES payment_account(account_number)
);

CREATE INDEX IF NOT EXISTS idx_balance_account_number ON  balance(account_number);