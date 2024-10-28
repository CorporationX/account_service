ALTER TABLE free_account_numbers
ALTER COLUMN account_number TYPE BIGINT USING account_number::bigint;