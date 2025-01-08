CREATE TABLE IF NOT EXISTS account_number_sequence
(
    type    VARCHAR(32) NOT NULL PRIMARY KEY,
    current BIGINT      NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);