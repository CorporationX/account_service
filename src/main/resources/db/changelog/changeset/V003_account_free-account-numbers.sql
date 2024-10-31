CREATE TABLE IF NOT EXISTS free_account_numbers(
    account_type varchar(24) NOT NULL,
    number varchar(20) NOT NULL UNIQUE,

    CONSTRAINT an_pk PRIMARY KEY (account_type, number)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence(
    account_type varchar(24) NOT NULL PRIMARY KEY,
    counter bigint DEFAULT 0 NOT NULL CHECK(counter <= 999999999998),
    version bigint DEFAULT 1
);

INSERT INTO account_numbers_sequence (account_type)
VALUES ('CURRENCY_ACCOUNT'),
       ('INDIVIDUAL_ACCOUNT'),
       ('LEGAL_ENTITY_ACCOUNT');
