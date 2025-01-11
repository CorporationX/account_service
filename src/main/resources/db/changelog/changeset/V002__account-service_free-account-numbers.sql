CREATE TABLE IF NOT EXISTS free_account_numbers
(
    type           VARCHAR(32) NOT NULL,
    account_number VARCHAR(20) NOT NULL CHECK (length(account_number) >= 12),
    PRIMARY KEY (type, account_number)
);