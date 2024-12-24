CREATE TABLE interest_rate
(
    id                      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    interest_rate           DOUBLE PRECISION NOT NULL,
    changed_by_user_history TEXT             NOT NULL
);

CREATE TABLE tariff
(
    id                      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tariff_type             VARCHAR(64) NOT NULL,
    interest_rate_id        BIGINT      NOT NULL,
    rate_history            TEXT        NOT NULL,
    changed_by_user_history TEXT        NOT NULL,

    CONSTRAINT fk_tariff_interest_rate FOREIGN KEY (interest_rate_id) REFERENCES interest_rate (id)
);

CREATE TABLE savings_account
(
    id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id         BIGINT         NOT NULL,
    tariff_id          BIGINT         NOT NULL,
    tariff_history     TEXT           NOT NULL,
    balance            DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    last_interest_date TIMESTAMPTZ             DEFAULT CURRENT_TIMESTAMP,
    version            NUMERIC,
    created_at         TIMESTAMPTZ             DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMPTZ             DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_savings_account_tariff FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    CONSTRAINT fk_savings_account_account FOREIGN KEY (account_id) REFERENCES account (id)
);