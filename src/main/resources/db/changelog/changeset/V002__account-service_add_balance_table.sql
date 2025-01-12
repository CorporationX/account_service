CREATE TABLE IF NOT EXISTS balance (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) NOT NULL,
    authorization_balance DECIMAL(15,2),
    fact_balance DECIMAL(15,2),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL,

    CONSTRAINT balance_fk FOREIGN KEY(account_number) REFERENCES account(number)
);