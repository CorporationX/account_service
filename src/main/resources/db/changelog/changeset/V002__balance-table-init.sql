CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE balance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL,
    authorized_amount NUMERIC(30, 10) NOT NULL DEFAULT 0,
    actual_amount NUMERIC(30, 10) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT current_timestamp,
    updated_at TIMESTAMP DEFAULT current_timestamp,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);