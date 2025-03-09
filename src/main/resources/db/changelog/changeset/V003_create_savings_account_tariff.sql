CREATE TABLE if NOT EXISTS savings_account
(
    id SERIAL PRIMARY KEY,
    account_number VARCHAR(64) UNIQUE NOT NULL,
    account_id bigint NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0,
    tariff_history TEXT,
    last_interest_date TIMESTAMP,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE if NOT EXISTS tariff
(
    id SERIAL PRIMARY KEY,
    tariff_name VARCHAR(255) NOT NULL,
    rate_history TEXT
);

CREATE INDEX IF NOT EXISTS idx_savings_account_account_id ON savings_account(account_id);