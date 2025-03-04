CREATE TABLE if NOT EXISTS free_account_numbers (
    account_type   VARCHAR(32) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE CHECK (char_length(account_number) >= 12),

    CONSTRAINT free_account_pk PRIMARY KEY(account_type, account_number)
);
