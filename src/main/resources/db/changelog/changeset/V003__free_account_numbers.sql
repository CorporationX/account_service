CREATE TABLE free_account_numbers (
    type VARCHAR(32) not null,
    account_number BIGINT not null,
    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE account_numbers_sequence (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    counter bigint not null DEFAULT 1
);