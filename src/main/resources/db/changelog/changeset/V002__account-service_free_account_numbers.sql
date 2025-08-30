CREATE TABLE free_account_numbers (
    type VARCHAR(32) NOT NULL,
    account_number BIGINT NOT NULL,

    CONSTRAINT free_acc_pk PRIMARY KEY (type, account_number)
);

CREATE INDEX idx_free_acc_number ON free_account_numbers (account_number);