CREATE TABLE IF NOT EXISTS free_account_numbers (
    type VARCHAR(32) NOT NULL,
    account_number BIGINT NOT NULL,
    PRIMARY KEY (type, account_number)
);
