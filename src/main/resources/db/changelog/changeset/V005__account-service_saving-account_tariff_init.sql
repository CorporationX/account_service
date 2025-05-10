CREATE TABLE IF NOT EXISTS tariff (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(256) NOT NULL,
    rate_history    JSONB NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS savings_account (
    id                  BIGSERIAL PRIMARY KEY,
    account_number      VARCHAR(20) NOT NULL UNIQUE REFERENCES payment_account(account_number),
    balance             DECIMAL(19, 4) NOT NULL DEFAULT 0,
    tariff_history      JSONB NOT NULL,
    last_interest_date  TIMESTAMP,
    version BIGINT      NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);