CREATE TABLE IF NOT EXISTS account_number_sequence(
    account_type VARCHAR(20) PRIMARY KEY,
    current_value bigint NOT NULL DEFAULT 0
);