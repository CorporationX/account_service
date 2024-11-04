CREATE TABLE free_account_numbers (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    account_number BIGINT NOT NULL
);