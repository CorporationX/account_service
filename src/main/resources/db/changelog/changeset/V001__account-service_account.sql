CREATE SEQUENCE IF NOT EXISTS account_gen START WITH 1 INCREMENT BY 10;

CREATE TABLE IF NOT EXISTS account
(
    id                  BIGINT PRIMARY KEY DEFAULT nextval('account_gen'),
    account_number      BIGINT UNIQUE NOT NULL,
    balance_id          BIGINT NOT NULL,
    account_owner_id    BIGINT,
    account_type        VARCHAR(255) NOT NULL,
    currency            VARCHAR(255) NOT NULL,
    account_status      VARCHAR(255) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMP,
    version             BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS balance
(
    id                  BIGINT PRIMARY KEY DEFAULT nextval('account_gen'),
    account_id          BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS owner_account (
    id                  BIGINT PRIMARY KEY DEFAULT nextval('account_gen'),
    owner_id            BIGINT NOT NULL,
    owner_type          VARCHAR(255) NOT NULL
);

ALTER TABLE account
    ADD CONSTRAINT fk_balance FOREIGN KEY (balance_id) REFERENCES balance(id),
    ADD CONSTRAINT fk_owner_account FOREIGN KEY (account_owner_id) REFERENCES owner_account(id);
    CREATE INDEX owner_idx ON account (account_owner_id)