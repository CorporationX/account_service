CREATE TABLE free_account_numbers (
    id SERIAL PRIMARY KEY,
    account_type VARCHAR(20) NOT NULL,
    free_account_number VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE account_numbers_sequence (
    account_type VARCHAR(20) PRIMARY KEY,
    current_counter VARCHAR(20) NOT NULL
);