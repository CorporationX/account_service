CREATE TABLE free_account_number
(
    type           VARCHAR(128)                                     NOT NULL,
    account_number VARCHAR(20) CHECK (length(account_number) >= 12) NOT NULL,
    PRIMARY KEY (type, account_number)
);

CREATE TABLE account_numbers_sequence
(
    type           VARCHAR(128) PRIMARY KEY,
    sequence_value BIGINT NOT NULL DEFAULT 0
);

INSERT INTO account_numbers_sequence (type)
VALUES ('CHECKING_INDIVIDUAL'),
       ('CHECKING_CORPORATE'),
       ('SAVINGS_ACCOUNT'),
       ('CURRENCY_ACCOUNT'),
       ('INVESTMENT_ACCOUNT');