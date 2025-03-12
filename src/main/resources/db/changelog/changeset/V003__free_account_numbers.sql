CREATE TABLE free_account_numbers (
    type VARCHAR(16) NOT NULL,
    account_number BIGINT NOT NULL,

    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE account_number_sequence (
    type VARCHAR(16) NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 1
);