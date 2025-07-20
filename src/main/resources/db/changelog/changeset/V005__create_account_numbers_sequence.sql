CREATE TABLE IF NOT EXISTS account_numbers_sequence (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_balance_type VARCHAR(6) UNIQUE NOT NULL,
    accounts_count INT4 NOT NULL
);
