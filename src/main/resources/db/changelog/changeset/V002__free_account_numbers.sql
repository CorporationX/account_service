CREATE TABLE IF NOT EXISTS free_account_numbers(
    account_type VARCHAR(20) NOT NULL,
    account_number bigint NOT NULL,
    PRIMARY KEY (account_type, account_number)
);