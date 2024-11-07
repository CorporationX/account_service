ALTER TABLE balance_audit
    ALTER COLUMN account_id DROP NOT NULL,
    ALTER COLUMN request_id DROP NOT NULL;
