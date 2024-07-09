CREATE TABLE IF NOT EXISTS free_account_numbers
(
    id BIGSERIAL PRIMARY KEY,
    number  VARCHAR(20)  NOT NULL UNIQUE,
    type    VARCHAR(128) NOT NULL,
    version INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence
(
    id BIGSERIAL PRIMARY KEY,
    sequence BIGINT       NOT NULL,
    type     VARCHAR(128) NOT NULL UNIQUE,
    code     CHAR(4)      NOT NULL UNIQUE,
    version  INT          NOT NULL
);