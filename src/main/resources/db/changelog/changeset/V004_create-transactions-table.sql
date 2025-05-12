CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    balance_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(16) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_amount CHECK (amount != 0),
    CONSTRAINT fk_transactions_balance FOREIGN KEY (balance_id) REFERENCES balance(id)
);

CREATE IF NOT EXISTS INDEX transactions_balance_id_idx ON transactions (balance_id);
CREATE IF NOT EXISTS INDEX transactions_type_idx ON transactions (type);
CREATE IF NOT EXISTS INDEX transactions_created_at_idx ON transactions (created_at);