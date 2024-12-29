CREATE TABLE account_numbers_sequence (
    type VARCHAR(32) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    counter BIGINT NOT NULL DEFAULT 1,

    CONSTRAINT account_numbers_seq_pk PRIMARY KEY (type, currency)
);