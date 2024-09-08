ALTER TABLE account
ADD CONSTRAINT fk_account_balance FOREIGN KEY (balance) REFERENCES balance(id);