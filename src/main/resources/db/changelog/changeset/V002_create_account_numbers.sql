CREATE TABLE if NOT EXISTS free_account_numbers (
    invoice_type   VARCHAR(32) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE CHECK (char_length(account_number) >= 12),

    CONSTRAINT free_account_pk PRIMARY KEY(invoice_type, account_number)
);

CREATE TABLE if NOT EXISTS account_numbers_sequence (
   id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
   invoice_type    VARCHAR(32) NOT NULL,
   current_counter BIGINT NOT NULL DEFAULT 1,
   version         BIGINT NOT NULL DEFAULT 1,
   updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO account_numbers_sequence (invoice_type, current_counter)
VALUES
      ('DEBIT', 1),
      ('CREDIT', 1),
      ('SAVINGS', 1),
      ('MORTGAGE', 1),
      ('CORPORATE', 1),
      ('TRADING', 1);
