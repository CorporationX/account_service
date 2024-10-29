CREATE TYPE account_type AS ENUM ('DEBIT', 'SAVINGS');

CREATE TABLE free_account_numbers (
    id SERIAL PRIMARY KEY,
    account_type account_type NOT NULL,
    free_account_number BIGINT UNIQUE NOT NULL
);

CREATE TABLE account_numbers_sequence (
    account_type account_type PRIMARY KEY,
    current_counter BIGINT NOT NULL
);