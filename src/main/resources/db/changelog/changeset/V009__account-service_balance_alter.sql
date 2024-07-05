ALTER TABLE balance
    ALTER COLUMN authorization_balance TYPE NUMERIC USING authorization_balance::numeric,
    ALTER COLUMN actual_balance TYPE NUMERIC USING actual_balance::numeric;