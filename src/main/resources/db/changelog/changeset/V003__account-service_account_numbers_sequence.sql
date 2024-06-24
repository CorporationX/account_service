CREATE TABLE account_numbers_sequence (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    counter BIGINT,
    type VARCHAR(24) NOT NULL UNIQUE,
    version BIGINT NOT NULL DEFAULT 0
);

INSERT INTO account_numbers_sequence (counter, type)
VALUES
    (1, 'CURRENT_INDIVIDUALS'),
    (1, 'CURRENT_LEGAL'),
    (1, 'FOREIGN_CURRENCY'),
    (1, 'DEPOSIT');