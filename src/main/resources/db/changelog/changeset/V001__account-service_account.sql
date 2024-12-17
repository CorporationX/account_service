CREATE TABLE IF NOT EXISTS free_account_numbers
(
id BIGSERIAL PRIMARY KEY,
type VARCHAR(32) NOT NULL,
account_number VARCHAR(20)
);