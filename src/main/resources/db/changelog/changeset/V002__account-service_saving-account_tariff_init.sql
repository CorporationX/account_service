CREATE TABLE IF NOT EXISTS tariff (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(256) NOT NULL,
    rate_history    JSONB NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS saving_account (
    id                  BIGSERIAL PRIMARY KEY,
    account_id          BIGINT NOT NULL UNIQUE REFERENCES account(id),
    balance             DECIMAL(19, 4) NOT NULL DEFAULT 0,
    tariff_history      JSONB NOT NULL,
    last_interest_date  TIMESTAMP,
    version BIGINT      NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);