CREATE TABLE free_account_numbers (
    type VARCHAR(32) NOT NULL,
    account_number BIGINT NOT NULL,

    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE  account_number_sequence (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL DEFAULT 1
);

INSERT INTO account_number_sequence (type, counter)
VALUES
    ('PERSONAL_CHECKING', 0),
    ('BUSINESS_CHECKING', 0),
    ('SAVINGS', 0),
    ('CURRENCY', 0),
    ('DEPOSIT', 0);