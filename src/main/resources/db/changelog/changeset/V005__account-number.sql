CREATE TABLE free_account_number
(
    id             SERIAL PRIMARY KEY,
    type           VARCHAR(128) NOT NULL,
    account_number VARCHAR(20)  NOT NULL,
    version        BIGINT       NOT NULL DEFAULT 0,

    UNIQUE (type, account_number),
    CHECK (account_number ~ '^[0-9]+$' AND LENGTH(account_number) >= 12
        )
);

CREATE TABLE account_unique_number_counter
(
    type    VARCHAR(128) PRIMARY KEY NOT NULL,
    counter BIGINT                   NOT NULL DEFAULT 0
);

INSERT INTO account_unique_number_counter (type)
VALUES ('DEBIT'),
       ('SAVINGS'),
       ('CREDIT'),
       ('LOAN'),
       ('INVESTMENT'),
       ('CHECKING'),
       ('BUSINESS'),
       ('RETIREMENT'),
       ('FOREX'),
       ('ESCROW');

