CREATE TABLE IF NOT EXISTS account_balance (

    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id bigint NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    current_balance DECIMAL(16) NOT NULL DEFAULT 0,
    available_balance DECIMAL(16) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT positive_balance CHECK (current_balance >= 0 AND available_balance >= 0),
    CONSTRAINT unique_account UNIQUE (account_id)

);