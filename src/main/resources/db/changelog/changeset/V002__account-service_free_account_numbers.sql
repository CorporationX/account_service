CREATE TABLE IF NOT EXISTS free_account_numbers (
    account_type VARCHAR(8) NOT NULL,
    account_number BIGINT NOT NULL,
    CONSTRAINT free_account_numbers_pk PRIMARY KEY (account_type, account_number)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequences (
    account_type VARCHAR(8) PRIMARY KEY NOT NULL,
    counter BIGINT DEFAULT 0 NOT NULL
);

INSERT INTO account_numbers_sequences (account_type) VALUES
  ('SAVINGS'),
  ('DEBIT');