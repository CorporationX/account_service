CREATE TABLE IF NOT EXISTS free_account_numbers (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_balance_type VARCHAR(6) NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL
);