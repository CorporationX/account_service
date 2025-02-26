CREATE TABLE IF NOT EXISTS free_account_numbers(
    account_type varchar(20) NOT NULL,
    account_number VARCHAR(20) NOT NULL,
    PRIMARY KEY (account_type, account_number),
    CONSTRAINT account_type_check CHECK (account_type IN ('INDIVIDUAL', 'LEGAL', 'CURRENCY'))
);