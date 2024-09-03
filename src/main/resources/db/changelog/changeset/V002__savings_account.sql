CREATE TABLE savings_account
(
    id                             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id                     BIGINT NOT NULL,
    tariff_history                 TEXT   NOT NULL,
    last_interest_calculation_date TIMESTAMP,
    version                        INT    NOT NULL,
    created_at                     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_savings_account_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE TABLE tariff
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name         VARCHAR(50) NOT NULL,
    rate_history TEXT        NOT NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO tariff (name, rate_history) VALUES ('BASIC', '[5]');
INSERT INTO tariff (name, rate_history) VALUES ('STANDARD', '[6.5]');
INSERT INTO tariff (name, rate_history) VALUES ('PREMIUM', '[8]');

CREATE INDEX idx_savings_account_account_id ON savings_account(account_id);
