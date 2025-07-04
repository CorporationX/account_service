CREATE TABLE balance (
    id bigserial PRIMARY KEY,
    account_number varchar(20),
    authorization_balance DECIMAL NOT NULL  DEFAULT 0.00,
    actual_balance DECIMAL NOT NULL DEFAULT 0.00,
    created_at timestamptz NOT NULL DEFAULT current_timestamp,
    updated_at timestamptz NOT NULL DEFAULT current_timestamp,
    version int NOT NULL DEFAULT 0,

    CONSTRAINT fk_account_number FOREIGN KEY (account_number) REFERENCES accounts (number)
);