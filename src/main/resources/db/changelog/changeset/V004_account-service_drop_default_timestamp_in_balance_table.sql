ALTER TABLE balance
    ALTER COLUMN created_at DROP DEFAULT,
    ALTER COLUMN updated_at DROP DEFAULT,
    ALTER COLUMN actual_balance DROP DEFAULT,
    ALTER COLUMN authorized_balance DROP DEFAULT;

ALTER TABLE account
    ALTER COLUMN created_at DROP DEFAULT,
    ALTER COLUMN updated_at DROP DEFAULT;