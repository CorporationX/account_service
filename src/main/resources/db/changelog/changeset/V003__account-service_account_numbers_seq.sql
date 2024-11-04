CREATE TABLE account_numbers_sequence(
    type VARCHAR(32) PRIMARY KEY,
    counter BIGINT NOT NULL DEFAULT 1,
    version BIGINT NOT NULL DEFAULT 1
)