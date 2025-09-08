CREATE TABLE balance (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL UNIQUE,
    authorized_balance NUMERIC(19, 4) NOT NULL DEFAULT 0.00,
    actual_balance NUMERIC(19, 4) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_balance_account
        FOREIGN KEY (account_id)
        REFERENCES account(id)
        ON DELETE CASCADE
);

-- Индекс для быстрого поиска по account_id
CREATE INDEX idx_balance_account_id ON balance(account_id);

-- Индекс для отслеживания изменений
CREATE INDEX idx_balance_updated_at ON balance(updated_at);