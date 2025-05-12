CREATE TABLE free_account_number (
    account_type VARCHAR(50) NOT NULL,
    account_number BIGINT NOT NULL,
    CONSTRAINT fre_acc_pk PRIMARY KEY (account_type, account_number)
);

CREATE TABLE account_number_sequence (
    account_type VARCHAR(50) PRIMARY KEY,
    last_value BIGINT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0
);