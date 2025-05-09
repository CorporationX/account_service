DROP INDEX IF EXISTS idx_balance_audit_created_at;

CREATE INDEX IF NOT EXISTS idx_balance_audit_account_id
    ON balance_audit (account_id);