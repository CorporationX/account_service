CREATE TABLE public.free_account_numbers (
    accounttype VARCHAR(32) NOT NULL,
    account_number BIGINT NOT NULL,
    CONSTRAINT free_acc_pk PRIMARY KEY (accounttype, account_number)
);
