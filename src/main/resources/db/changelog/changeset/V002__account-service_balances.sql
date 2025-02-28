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

CREATE OR REPLACE FUNCTION update_balances_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();  -- Устанавливаем текущее время в поле updated_at
    RETURN NEW;  -- Возвращаем изменённую запись
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION increment_version()
RETURNS TRIGGER AS $$
BEGIN
    NEW.version := OLD.version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_balances_timestamp
BEFORE UPDATE ON balances
FOR EACH ROW
EXECUTE FUNCTION update_balances_timestamp();

CREATE TRIGGER trigger_increment_version
BEFORE UPDATE ON balances
FOR EACH ROW
EXECUTE FUNCTION increment_version();