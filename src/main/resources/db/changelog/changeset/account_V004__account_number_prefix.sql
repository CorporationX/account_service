CREATE TABLE IF NOT EXISTS account_numbers_prefix
(
    type   VARCHAR(32) NOT NULL PRIMARY KEY,
    prefix VARCHAR(10) NOT NULL UNIQUE
);

INSERT INTO account_numbers_prefix (type, prefix)
VALUES ('CURRENT', '1100'),
       ('SAVINGS', '2200'),
       ('FOREIGN_EXCHANGE', '3300'),
       ('CREDIT', '4400'),
       ('JOINT', '5500'),
       ('BUSINESS', '6600'),
       ('ESCROW', '7700'),
       ('DEPOSIT', '8800');