CREATE TABLE savings_account
(
    id                 BIGSERIAL PRIMARY KEY,
    account_id         BIGINT         NOT NULL UNIQUE,
    balance            NUMERIC(19, 2) NOT NULL,
    tariff_history     JSONB          NOT NULL,
    last_interest_date TIMESTAMP,
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version            INT            NOT NULL DEFAULT 0,

    CONSTRAINT fk_savings_account_account FOREIGN KEY (account_id) REFERENCES account (id)
);
