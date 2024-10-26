ALTER TABLE free_account_numbers
DROP CONSTRAINT free_account_numbers_account_number_check;

ALTER TABLE free_account_numbers
ADD CONSTRAINT account_number_range CHECK (account_number >= 100000000000 AND account_number <=  9223372036854775807);