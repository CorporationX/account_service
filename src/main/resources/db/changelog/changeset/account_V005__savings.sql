CREATE TABLE IF NOT EXISTS savings_account
(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    tariff_history VARCHAR NOT NULL,
    last_income_at DATE DEFAULT CURRENT_DATE,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id)
);
  CREATE INDEX idx_savings_account_id ON savings_account (account_id);

CREATE TABLE IF NOT EXISTS tariff
(
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(32) NOT NULL,
    rate_history VARCHAR NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_tariff_title ON tariff (title);

