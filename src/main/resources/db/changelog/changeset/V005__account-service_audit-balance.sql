CREATE TABLE balance_audit (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id BIGINT NOT NULL,
    version_balance INTEGER DEFAULT 0,
    authorization_balance NUMERIC,
    actual_balance NUMERIC,
    operation VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT current_timestamp
);

CREATE INDEX account_id_idx ON balance_audit (account_id);