CREATE TABLE free_account_numbers (
    type VARCHAR(32) NOT NULL,
    account_number VARCHAR(32) NOT NULL,
    currency VARCHAR(16) NOT NULL,

    CONSTRAINT free_account_numbers_pk PRIMARY KEY (type, account_number, currency)
);