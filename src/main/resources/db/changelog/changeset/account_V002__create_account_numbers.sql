CREATE TABLE IF NOT EXISTS free_account_numbers (
    type VARCHAR(16) NOT NULL,
    account_number BIGINT NOT NULL,

    CONSTRAINT free_acc_num_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence (
    type VARCHAR(16) NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL DEFAULT 1
);

INSERT INTO account_numbers_sequence (type)
VALUES
        ('DEBIT'),
        ('SAVINGS');