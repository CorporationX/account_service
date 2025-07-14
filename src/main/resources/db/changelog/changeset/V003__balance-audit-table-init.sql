CREATE TABLE balance_audit (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    balance_version BIGINT NOT NULL,
    authorized_amount NUMERIC(30, 10) NOT NULL,
    actual_amount NUMERIC(30, 10) NOT NULL,
    balance_change_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    FOREIGN KEY (account_number) REFERENCES Account (account_number) ON DELETE CASCADE
);