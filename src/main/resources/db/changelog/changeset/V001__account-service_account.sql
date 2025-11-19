-- Create enum types
CREATE TYPE account_type AS ENUM (
    'PERSONAL_CHECKING',      -- Расчетный счет для физ. лиц
    'BUSINESS_CHECKING',      -- Расчетный счет для юр. лиц
    'SAVINGS',                -- Сберегательный счет
    'CURRENCY',               -- Валютный счет
    'DEPOSIT'                 -- Депозитный счет
);

CREATE TYPE account_status AS ENUM (
    'ACTIVE',                 -- Действующий
    'FROZEN',                 -- Замороженный
    'BLOCKED',                -- Заблокированный
    'CLOSED'                  -- Закрытый
);

CREATE TYPE owner_type AS ENUM (
    'USER',                   -- Владелец - пользователь
    'PROJECT'                 -- Владелец - проект
);

-- Create account table
CREATE TABLE account (
                         id BIGSERIAL PRIMARY KEY,
                         account_number VARCHAR(20) NOT NULL UNIQUE,
                         owner_id BIGINT NOT NULL,
                         owner_type owner_type NOT NULL,
                         account_type account_type NOT NULL,
                         currency VARCHAR(3) NOT NULL,
                         status account_status NOT NULL DEFAULT 'ACTIVE',
                         balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         closed_at TIMESTAMP,
                         version BIGINT NOT NULL DEFAULT 0,

    -- Constraints
    CONSTRAINT chk_account_number_format CHECK (account_number ~ '^\d{12,20}$'),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_closed_at_logic CHECK (
        (status = 'CLOSED' AND closed_at IS NOT NULL) OR
        (status != 'CLOSED' AND closed_at IS NULL)
    )
);

-- Create indexes
CREATE INDEX idx_account_owner ON account(owner_id, owner_type);
CREATE INDEX idx_account_status ON account(status);
CREATE INDEX idx_account_created_at ON account(created_at);
CREATE INDEX idx_account_number ON account(account_number);

-- Add comments
COMMENT ON TABLE account IS 'Платежные счета пользователей и проектов';
COMMENT ON COLUMN account.account_number IS 'Номер счета (12-20 цифр)';
COMMENT ON COLUMN account.owner_id IS 'ID владельца (пользователя или проекта)';
COMMENT ON COLUMN account.owner_type IS 'Тип владельца';
COMMENT ON COLUMN account.balance IS 'Текущий баланс счета';
COMMENT ON COLUMN account.version IS 'Версия для Optimistic Locking';