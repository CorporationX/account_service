CREATE TABLE balance (
    id bigserial PRIMARY KEY,
    account_number varchar(20),
    authorization_balance DECIMAL NOT NULL,
    actual_balance DECIMAL NOT NULL,
    created_at timestamptz NOT NULL DEFAULT current_timestamp,
    updated_at timestamptz NOT NULL,
    version int NOT NULL
);