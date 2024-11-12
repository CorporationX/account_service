CREATE TABLE free_account_numbers (
    type VARCHAR(20) NOT NULL,
    account_number VARCHAR(20) UNIQUE NOT NULL,

    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE TABLE account_numbers_sequence (
    type VARCHAR(20) NOT NULL,
    counter BIGINT NOT NULL DEFAULT 1,

    CONSTRAINT account_numbers_sequence_pk PRIMARY KEY (type, counter)
); 