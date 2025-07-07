CREATE TABLE IF NOT EXISTS account_numbers_sequence (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_balance_type varchar(32) NOT NULL,
    accounts_count bigint UNIQUE NOT NULL
);
