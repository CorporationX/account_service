CREATE TABLE free_account_numbers
(
    account_number VARCHAR(20) NOT NULL PRIMARY KEY,
    account_type   VARCHAR(8)  NOT NULL
);

CREATE TABLE account_numbers_sequence
(
    account_type   VARCHAR(8) NOT NULL PRIMARY KEY,
    current_number BIGINT     NOT NULL DEFAULT 0
);

INSERT INTO account_numbers_sequence (account_type)
VALUES ('5536'),
       ('4276'),
       ('5200');