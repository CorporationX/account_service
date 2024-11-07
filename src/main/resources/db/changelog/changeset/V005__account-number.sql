CREATE TABLE free_account_number
(
    type           VARCHAR(128) NOT NULL,
    digit_sequence VARCHAR(20)  NOT NULL,
    version        BIGINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (type, digit_sequence) ,
    CHECK (digit_sequence ~ '^[0-9]+$' AND LENGTH(digit_sequence) >= 12)
);

CREATE TABLE account_unique_number_counter
(
    type    VARCHAR(128) PRIMARY KEY NOT NULL,
    counter BIGINT                   NOT NULL DEFAULT 0,
    generation_state BOOLEAN NOT NULL DEFAULT FALSE
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