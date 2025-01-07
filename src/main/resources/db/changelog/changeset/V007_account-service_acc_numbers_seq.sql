CREATE TABLE IF NOT EXISTS account_numbers_sequence
(
type VARCHAR(32) NOT NULL,
counter BIGINT NOT NULL DEFAULT 0,
version int NOT NULL
);