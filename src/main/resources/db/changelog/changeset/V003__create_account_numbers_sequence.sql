CREATE TABLE account_numbers_sequence (
                                          account_type VARCHAR(10) NOT NULL PRIMARY KEY,
                                          current_sequence BIGINT NOT NULL DEFAULT 0,
                                          version BIGINT NOT NULL DEFAULT 0,
                                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE account_numbers_sequence
    ADD CONSTRAINT chk_current_sequence_non_negative
        CHECK (current_sequence >= 0);

ALTER TABLE account_numbers_sequence
    ADD CONSTRAINT chk_account_type_length_sequence
        CHECK (char_length(account_type) >= 3);

ALTER TABLE account_numbers_sequence
    ADD CONSTRAINT chk_account_type_values_sequence
        CHECK (account_type IN ('DEBIT', 'SAVINGS', 'CREDIT', 'BUSINESS'));

ALTER TABLE account_numbers_sequence
    ADD CONSTRAINT chk_version_non_negative
        CHECK (version >= 0);

CREATE TRIGGER update_account_numbers_sequence_updated_at
    BEFORE UPDATE ON account_numbers_sequence
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE INDEX idx_account_numbers_sequence_type
    ON account_numbers_sequence (account_type);

CREATE INDEX idx_account_numbers_sequence_updated_at
    ON account_numbers_sequence (updated_at);

CREATE INDEX idx_account_numbers_sequence_type_sequence
    ON account_numbers_sequence (account_type, current_sequence);

COMMENT ON TABLE account_numbers_sequence IS 'Table for storing account number counters/sequences';
COMMENT ON COLUMN account_numbers_sequence.account_type IS 'Account type (DEBIT, SAVINGS, CREDIT, BUSINESS)';
COMMENT ON COLUMN account_numbers_sequence.current_sequence IS 'Current sequence counter value';
COMMENT ON COLUMN account_numbers_sequence.version IS 'Version for optimistic locking (JPA @Version)';
COMMENT ON COLUMN account_numbers_sequence.created_at IS 'Record creation timestamp';
COMMENT ON COLUMN account_numbers_sequence.updated_at IS 'Last update timestamp (auto-updated by trigger)';

COMMENT ON INDEX idx_account_numbers_sequence_type IS 'Primary access pattern for sequence lookups';
COMMENT ON INDEX idx_account_numbers_sequence_updated_at IS 'Optimizes monitoring queries by update time';
COMMENT ON INDEX idx_account_numbers_sequence_type_sequence IS 'Optimizes optimistic locking UPDATE queries';

INSERT INTO account_numbers_sequence (account_type, current_sequence, created_at, updated_at)
VALUES
    ('DEBIT', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SAVINGS', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('CREDIT', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BUSINESS', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (account_type) DO NOTHING;