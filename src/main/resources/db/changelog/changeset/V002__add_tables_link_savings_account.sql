CREATE INDEX IF NOT EXISTS idx_account_owner_id ON account(owner_id);

CREATE TABLE IF NOT EXISTS savings_account (
    account_id bigint PRIMARY KEY,
    account_number varchar(128) NOT NULL UNIQUE,
    balance decimal NOT NULL default 0,
    last_interest_accrual_at timestamptz,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE INDEX IF NOT EXISTS idx_created_at ON savings_account (created_at);
CREATE INDEX IF NOT EXISTS idx_last_interest_accrual_at ON savings_account (last_interest_accrual_at);

CREATE TABLE IF NOT EXISTS tariff (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    type_name varchar(128) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_type_name ON tariff (type_name);

CREATE TABLE IF NOT EXISTS tariff_history (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tariff_id bigint NOT NULL,
    savings_account_id bigint NOT NULL,
    applied_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_tariff_id FOREIGN KEY (tariff_id) REFERENCES tariff (id),
    CONSTRAINT fk_savings_account_id FOREIGN KEY (savings_account_id) REFERENCES savings_account (account_id)
);

CREATE INDEX IF NOT EXISTS idx_tariff_id ON tariff_history (tariff_id);
CREATE INDEX IF NOT EXISTS idx_tariff_history_by_account ON tariff_history (savings_account_id, applied_at DESC);

CREATE TABLE IF NOT EXISTS tariff_rate_history (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    tariff_id bigint NOT NULL,
    rate decimal(5, 2) NOT NULL,
    changed_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_tariff_history_id FOREIGN KEY (tariff_id) REFERENCES tariff (id)
);

CREATE INDEX IF NOT EXISTS idx_tariff_rate_history ON tariff_rate_history (tariff_id, changed_at DESC);
