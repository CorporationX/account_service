-- Создание таблицы balance
CREATE TABLE IF NOT EXISTS balance (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL CHECK (account_number ~ '^\d{12,20}$'),
    authorization_balance NUMERIC(19,4) NOT NULL,
    actual_balance NUMERIC(19,4) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp NOT NULL,
    balance_version BIGINT NOT NULL DEFAULT 0
    );

-- Создание таблицы account
CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL CHECK (account_number ~ '^\d{12,20}$'),
    user_id BIGINT,
    project_id BIGINT,
    type VARCHAR(50) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT current_timestamp NOT NULL,
    closed_at TIMESTAMPTZ,
    balance_id BIGINT,
    account_version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_balance FOREIGN KEY (balance_id) REFERENCES balance (id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_account_user_id ON account(user_id);
