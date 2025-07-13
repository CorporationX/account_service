CREATE TABLE savings_account (
    id BIGINT PRIMARY KEY,
    tariff_story JSONB,
    last_interest_accrual_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_savings_account_account FOREIGN KEY (id) REFERENCES accounts(id)
);

CREATE TABLE tariff (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  tariff_type VARCHAR(100) UNIQUE NOT NULL,
  version INT NOT NULL DEFAULT 0
);

CREATE TABLE tariff_rate_history (
  id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  tariff_id BIGINT NOT NULL,
  rate NUMERIC(5,2) NOT NULL,
  effective_date DATE NOT NULL,
  CONSTRAINT fk_tariff_rate_history_tariff FOREIGN KEY (tariff_id) REFERENCES tariff(id) ON DELETE CASCADE,
  CONSTRAINT uq_tariff_rate_history_tariff_effective_date UNIQUE (tariff_id, effective_date)
);

CREATE TABLE savings_account_tariff (
    savings_account_id BIGINT NOT NULL,
    tariff_id BIGINT NOT NULL,
    effective_date DATE NOT NULL,
    PRIMARY KEY (savings_account_id, tariff_id, effective_date),
    CONSTRAINT fk_savings_account_tariff_savings_account FOREIGN KEY (savings_account_id) REFERENCES savings_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_savings_account_tariff_tariff FOREIGN KEY (tariff_id) REFERENCES tariff(id) ON DELETE CASCADE
);
