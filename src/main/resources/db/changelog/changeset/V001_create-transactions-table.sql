CREATE TABLE transactions (

    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    balance_id bigint NOT NULL REFERENCES balance(id),
    amount DECIMAL(16, 2) NOT NULL,
    type VARCHAR(16) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
    comment TEXT NULL

    CONSTRAINT valid_amount CHECK (amount != 0)

    CREATE INDEX transactions_balance_id_idx ON transactions (balance_id);
    CREATE INDEX transactions_type_idx ON transactions (type);
    CREATE INDEX transactions_created_at_idx ON transactions (amount);
);