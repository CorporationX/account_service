CREATE TABLE account_number_sequence (
    type VARCHAR(32) NOT NULL PRIMARY KEY,
    counter BIGINT NOT NULL DEFAULT 0
);