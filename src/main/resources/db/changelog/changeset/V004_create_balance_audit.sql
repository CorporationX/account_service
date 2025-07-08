CREATE TABLE IF NOT EXISTS balance_audit (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    version BIGINT NOT NULL,
    authorized_balance NUMERIC(19,4) NOT NULL,
    actual_balance NUMERIC(19,4) NOT NULL,
    operation_id UUID NOT NULL,
    created_at timestamp DEFAULT current_timestamp,

    CONSTRAINT fk_balance_audit_account
    FOREIGN KEY (account_id)
    REFERENCES accounts (id)
    ON DELETE CASCADE

);

CREATE INDEX IF NOT EXISTS idx_balance_audit_account_created
    ON balance_audit (account_id, created_at);

CREATE INDEX IF NOT EXISTS idx_balance_audit_account_version
    ON balance_audit (account_id, version DESC);