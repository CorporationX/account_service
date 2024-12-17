CREATE TABLE IF NOT EXISTS free_account_numbers
(
    id BIGSERIAL PRIMARY KEY,
    account_type VARCHAR(32) NOT NULL,
    account_number VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS account_numbers_sequence
(
    id BIGSERIAL PRIMARY KEY,
    account_type VARCHAR(32) NOT NULL,
    current_value BIGINT NOT NULL,
);