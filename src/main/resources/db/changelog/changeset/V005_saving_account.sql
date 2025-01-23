CREATE TABLE tariff
(
    id              BIGINT      PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name            VARCHAR(64) NOT NULL,
    rate            NUMERIC     NOT NULL,
    rate_history    TEXT        NOT NULL,
    version         BIGINT      NOT NULL,
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE saving_accounts
(
    id              BIGINT      PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id      BIGINT      NOT NULL,
    tariff_id       BIGINT      NOT NULL,
    tariff_history  TEXT        NOT NULL,
    increased_at    TIMESTAMP,
    version         BIGINT,
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_saving_accounts_tariff FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    CONSTRAINT fk_saving_accounts_account FOREIGN KEY (account_id) REFERENCES account_schema.account (id)
);