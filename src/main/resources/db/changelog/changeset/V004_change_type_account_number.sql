ALTER TABLE free_account_number
    ALTER COLUMN account_number TYPE VARCHAR(20);

ALTER TABLE account_number_sequence
    ALTER COLUMN last_value TYPE VARCHAR(20);