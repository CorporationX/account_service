CREATE TABLE IF NOT EXISTS savings_account
(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT REFERENCES account(id) NOT NULL,
    tariff_history JSONB,
    last_interest_calculation TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);