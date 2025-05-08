CREATE TABLE account_numbers_sequence
(
    account_type  VARCHAR(4) PRIMARY KEY,
    current_value BIGINT NOT NULL,
    version       BIGINT NOT NULL DEFAULT 0
);
