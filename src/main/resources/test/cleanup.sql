DELETE FROM free_account_numbers;

UPDATE account_numbers_sequence
SET current_sequence = 0, version = 0, updated_at = CURRENT_TIMESTAMP
WHERE account_type IN ('DEBIT', 'SAVINGS', 'CREDIT', 'BUSINESS');