ALTER TABLE accounts DROP CONSTRAINT accounts_number_key;
ALTER TABLE accounts ADD CONSTRAINT accounts_number_account_type_key UNIQUE (number, account_type);