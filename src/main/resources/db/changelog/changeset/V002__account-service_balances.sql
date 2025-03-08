CREATE TABLE IF NOT EXISTS balances (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL,
    auth_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    actual_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_id) REFERENCES accounts(id)
);

comment on table balances is 'Балансы';
comment on column balances.id is 'Уникальный идентификатор записи баланса';
comment on column balances.account_id is 'Счёт, к которому относится баланс';
comment on column balances.auth_balance is 'Авторизационный баланс (заблокированные средства)';
comment on column balances.actual_balance is 'Фактический баланс (доступные средства)';
comment on column balances.version is 'Версия баланса (для оптимистичной блокировки)';
comment on column balances.created_at is 'Дата создания записи баланса';
comment on column balances.updated_at is 'Дата последнего изменения записи баланса';

CREATE INDEX idx_balances_account_id ON balances(account_id);