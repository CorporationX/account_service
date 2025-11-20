CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          account_number VARCHAR(20) NOT NULL UNIQUE,
                          owner_id BIGINT NOT NULL,
                          owner_type VARCHAR(32) NOT NULL,
                          account_type VARCHAR(128) NOT NULL,
                          currency VARCHAR(3) NOT NULL,
                          status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
                          balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          closed_at TIMESTAMP,
                          version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_account_number_format CHECK (account_number ~ '^\d{12,20}$'),
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

CREATE INDEX idx_account_owner ON accounts(owner_id, owner_type);
CREATE INDEX idx_account_status ON accounts(status);
CREATE INDEX idx_account_number ON accounts(account_number);

COMMENT ON TABLE accounts IS 'Платежные счета пользователей и проектов';
COMMENT ON COLUMN accounts.account_number IS 'Номер счета (12-20 цифр)';
COMMENT ON COLUMN accounts.owner_id IS 'ID владельца (пользователя или проекта)';
COMMENT ON COLUMN accounts.owner_type IS 'Тип владельца';
COMMENT ON COLUMN accounts.balance IS 'Текущий баланс счета';
COMMENT ON COLUMN accounts.version IS 'Версия для Optimistic Locking';