CREATE TABLE currency
(
    currency_code CHAR(3) PRIMARY KEY,
    currency_name VARCHAR(128) NOT NULL
);

INSERT INTO currency (currency_code, currency_name)
VALUES ('USD', 'United States Dollar'),
       ('EUR', 'Euro'),
       ('RUB', 'Russian Rouble'),
       ('CNY', 'Chinese Yuan');


CREATE TABLE account
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_number   VARCHAR(20)  NOT NULL UNIQUE,
    owner_type       VARCHAR(8)   NOT NULL,
    owner_id         BIGINT       NOT NULL,
    owner_name       VARCHAR(256) NOT NULL,
    account_type     VARCHAR(128) NOT NULL,
    account_currency CHAR(3)      NOT NULL,
    account_status   VARCHAR(32)  NOT NULL,
    created_at       timestamptz           DEFAULT current_timestamp,
    updated_at       timestamptz           DEFAULT current_timestamp,
    deleted_at       timestamptz           DEFAULT NULL,
    account_version  INT          NOT NULL DEFAULT 1,

    CONSTRAINT fk_account_currency FOREIGN KEY (account_currency) REFERENCES currency (currency_code)
);

CREATE INDEX owner_id_idx ON account (owner_id);

