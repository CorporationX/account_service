CREATE TABLE free_account_numbers
(
    type           VARCHAR(32) NOT NULL,
    account_number BIGINT      NOT NULL,
    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE account_number_sequence
(
    type    VARCHAR(32) NOT NULL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    counter BIGINT      NOT NULL
);