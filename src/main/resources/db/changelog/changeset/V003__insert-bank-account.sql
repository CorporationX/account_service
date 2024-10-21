ALTER TABLE balance ADD bank_account varchar(20);
ALTER TABLE balance ADD
    CONSTRAINT check_bank_account_length CHECK ( length(balance.bank_account) >= 12)