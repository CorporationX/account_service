CREATE TABLE IF NOT EXISTS account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    user_id BIGINT,
    project_id BIGINT,
    bank VARCHAR(64) NOT NULL,
    amount DECIMAL(19, 2) DEFAULT 0.00 NOT NULL,
    currency VARCHAR(3) NOT NULL,
    type VARCHAR(64) NOT NULL,
    status VARCHAR(64) DEFAULT 'ACTIVE',
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version INT DEFAULT 0,

    CONSTRAINT check_owner_user_or_project CHECK (
            (user_id IS NOT NULL AND project_id IS NULL) OR
            (user_id IS NULL AND project_id IS NOT NULL)
        )
);

CREATE INDEX user_account ON account (user_id);
CREATE INDEX project_account ON account (project_id);

-- Создаем аккаунт для пользователя с id 9
INSERT INTO account (account_number, user_id, bank, amount, currency, type, status)
VALUES ('TINKOFF123456789', 9, 'TINKOFF', 1000.00, 'USD', 'CHECKING', 'ACTIVE');

-- Создаем аккаунт для пользователя с id 5
INSERT INTO account (account_number, user_id, bank, currency, type, status)
VALUES ('SBERBANK987654321', 5, 'SBERBANK', 'EUR', 'SAVINGS', 'ACTIVE');