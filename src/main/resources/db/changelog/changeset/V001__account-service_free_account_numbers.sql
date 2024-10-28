CREATE TABLE free_account_numbers(
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL CHECK (LENGTH(account_number) BETWEEN 12 and 20) UNIQUE,
    account_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)