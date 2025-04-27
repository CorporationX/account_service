CREATE TABLE account_balance (

    id bigint PRIMARY KEY,
    account_id bigint NOT NULL REFERENCES account(id),
    current_balance DECIMAL(16) NOT NULL DEFAULT 0,
    available_balance DECIMAL(16) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
    CONSTRAINT positive_balance CHECK (current_balance >= 0 AND available_balance >= 0),
        UNIQUE (account_id)

);