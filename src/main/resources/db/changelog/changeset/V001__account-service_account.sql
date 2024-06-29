CREATE TABLE free_account_numbers
(
    type           VARCHAR(64) NOT NULL,
    account_number bigint      NOT NULL PRIMARY KEY UNIQUE
);

CREATE TABLE account_numbers_sequence
(
    account_type     VARCHAR(64) NOT NULL PRIMARY KEY UNIQUE,
    current_counter  bigint      NOT NULL,
    previous_counter bigint      NOT NULL,
    version          bigint
);

INSERT INTO account_numbers_sequence(account_type, current_counter, previous_counter)
VALUES ('SAVINGS', 0, 0),
       ('DEBIT', 0, 0)