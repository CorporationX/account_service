CREATE TABLE free_account_numbers (
                                      account_type VARCHAR(10) NOT NULL,
                                      account_number VARCHAR(20) NOT NULL,
                                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (account_type, account_number)
);

ALTER TABLE free_account_numbers
    ADD CONSTRAINT chk_account_number_format
        CHECK (account_number ~ '^[0-9]{12,20}$');

ALTER TABLE free_account_numbers
    ADD CONSTRAINT chk_account_type_length_free
        CHECK (char_length(account_type) >= 3);

ALTER TABLE free_account_numbers
    ADD CONSTRAINT chk_account_type_values_free
        CHECK (account_type IN ('DEBIT', 'SAVINGS', 'CREDIT', 'BUSINESS'));

CREATE INDEX idx_free_account_numbers_type_created_asc
    ON free_account_numbers (account_type, created_at ASC);

CREATE INDEX idx_free_account_numbers_type
    ON free_account_numbers (account_type);

CREATE INDEX idx_free_account_numbers_recent
    ON free_account_numbers (created_at)
    WHERE created_at >= (CURRENT_TIMESTAMP - INTERVAL '1 day');

CREATE INDEX idx_free_account_numbers_account_number
    ON free_account_numbers (account_number);

COMMENT ON TABLE free_account_numbers IS 'Table for storing pre-generated free account numbers';
COMMENT ON COLUMN free_account_numbers.account_type IS 'Account type (DEBIT, SAVINGS, CREDIT, BUSINESS)';
COMMENT ON COLUMN free_account_numbers.account_number IS 'Free account number (12-20 digits)';
COMMENT ON COLUMN free_account_numbers.created_at IS 'Record creation timestamp';

COMMENT ON INDEX idx_free_account_numbers_type_created_asc IS 'Optimizes FIFO retrieval of free numbers by type';
COMMENT ON INDEX idx_free_account_numbers_type IS 'Optimizes counting and existence checks by account type';
COMMENT ON INDEX idx_free_account_numbers_recent IS 'Optimizes queries for recently generated numbers';
COMMENT ON INDEX idx_free_account_numbers_account_number IS 'Optimizes account number validation and deduplication';