CREATE TABLE IF NOT EXISTS free_account_number (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_balance_type varchar(32) NOT NULL,
    account_number varchar(20) UNIQUE NOT NULL,
);