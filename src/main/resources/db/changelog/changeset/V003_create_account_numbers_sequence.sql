CREATE TABLE if NOT EXISTS account_numbers_sequence (
   id              BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
   account_type    VARCHAR(32) NOT NULL,
   current_counter BIGINT NOT NULL DEFAULT 1,
   version         BIGINT NOT NULL DEFAULT 1,
   updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO account_numbers_sequence (account_type, current_counter)
VALUES
      ('DEBIT', 1),
      ('CREDIT', 1),
      ('SAVINGS', 1),
      ('MORTGAGE', 1),
      ('CORPORATE', 1),
      ('TRADING', 1);
