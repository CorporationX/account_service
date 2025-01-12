CREATE TABLE balance_audit (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    balance_id BIGINT NOT NULL,
    balance_version INT NOT NULL,
    authorized_balance DECIMAL(19, 4) NOT NULL,
    operation_amount DECIMAL(19, 4) NOT NULL,
    transaction_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account FOREIGN KEY (balance_id) REFERENCES account(id) ON DELETE CASCADE,
    CONSTRAINT fk_transaction FOREIGN KEY (transaction_id) REFERENCES transaction(id) ON DELETE CASCADE
);
