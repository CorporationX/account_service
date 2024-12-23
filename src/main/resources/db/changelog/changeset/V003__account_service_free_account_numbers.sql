CREATE TABLE IF NOT EXISTS free_account_numbers
(
    id             BIGSERIAL PRIMARY KEY,
    account_type   VARCHAR(32) NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence
(
    account_type  VARCHAR(32) NOT NULL PRIMARY KEY,
    current_value BIGINT DEFAULT 0 NOT NULL,
    version       BIGINT DEFAULT 1 NOT NULL
);

INSERT INTO account_numbers_sequence(account_type)
VALUES ('INDIVIDUAL'),
       ('SAVINGS'),
       ('DEBIT'),
       ('LEGAL');