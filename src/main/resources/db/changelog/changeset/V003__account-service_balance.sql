CREATE TABLE IF NOT EXISTS balance (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    authorized_balance DECIMAL(20, 4) DEFAULT 0,
    actual_balance DECIMAL(20, 4) DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp NOT NULL,
    version bigint NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS balance_account_id_idx ON balance(account_id);
