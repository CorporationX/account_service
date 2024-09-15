CREATE TABLE tariff
(
    id           UUID         NOT NULL PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    rate_history TEXT         NOT NULL,
    created_at   timestamptz DEFAULT current_timestamp,
    updated_at   timestamptz DEFAULT current_timestamp
);

CREATE TABLE savings_account
(
    id                             UUID   NOT NULL PRIMARY KEY,
    account_id                     UUID   NOT NULL,
    tariff_history                 TEXT   NOT NULL,
    last_interest_calculation_date timestamptz,
    version                        BIGINT NOT NULL,
    created_at                     timestamptz DEFAULT current_timestamp,
    updated_at                     timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_account
        FOREIGN KEY (account_id)
            REFERENCES account (id)
            ON DELETE CASCADE
);

CREATE INDEX idx_savings_account_account_id ON savings_account (account_id);
