CREATE TABLE IF NOT EXISTS free_account_numbers
(
    type           VARCHAR(16) NOT NULL,
    account_number VARCHAR(20) NOT NULL,

    CONSTRAINT check_account_number_length
        CHECK (length(account_number) BETWEEN 12 AND 20),
    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE IF NOT EXISTS account_number_sequence
(
    type    VARCHAR(16) NOT NULL PRIMARY KEY,
    counter BIGINT      NOT NULL DEFAULT 0
);
