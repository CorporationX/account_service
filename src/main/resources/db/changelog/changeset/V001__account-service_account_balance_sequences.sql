CREATE SEQUENCE IF NOT EXISTS account_individual_numbers
    AS BIGINT
    INCREMENT BY 1
    CACHE 1
    NO CYCLE;

CREATE SEQUENCE IF NOT EXISTS account_company_numbers
    AS BIGINT
    INCREMENT BY 1
    CACHE 1
    NO CYCLE;

CREATE TABLE IF NOT EXISTS account
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number   BIGINT UNIQUE NOT NULL,
    balance_id       BIGINT        NOT NULL,
    account_owner_id BIGINT        NOT NULL,
    account_type     VARCHAR(20)   NOT NULL,
    currency         VARCHAR(20)   NOT NULL,
    account_status   VARCHAR(20)   NOT NULL,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at        TIMESTAMP,
    version          BIGINT        NOT NULL
);

CREATE TABLE IF NOT EXISTS balance
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id BIGINT
);

CREATE TABLE IF NOT EXISTS owner_account
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    owner_id   BIGINT      NOT NULL,
    owner_type VARCHAR(20) NOT NULL
);