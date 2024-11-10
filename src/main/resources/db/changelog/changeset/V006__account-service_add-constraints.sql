ALTER TABLE balance
    ALTER COLUMN authorized_balance SET NOT NULL,
    ALTER COLUMN actual_balance SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE pending_operations
    ALTER COLUMN amount SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN state SET NOT NULL;

ALTER TABLE pending_operations_account_service
    ALTER COLUMN amount SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL,
    ALTER COLUMN state SET NOT NULL,
    ALTER COLUMN operation_key SET NOT NULL;
