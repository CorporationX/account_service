CREATE TABLE account_numbers_sequence (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    type VARCHAR(64) NOT NULL UNIQUE,
    current BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE free_account_numbers (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    type VARCHAR(64) NOT NULL,
    account_number CHAR(16) UNIQUE NOT NULL
);

INSERT INTO account_numbers_sequence (type) VALUES
('DEBIT'),
('SAVINGS');