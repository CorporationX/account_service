CREATE TABLE IF NOT EXISTS balance_audit
(
    id              bigint          PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    balance_id      bigint          NOT NULL,
    account_id      INT             NOT NULL,
    auth_balance    DECIMAL(15, 2)  NOT NULL DEFAULT 0.00,
    actual_balance  DECIMAL(15, 2)  NOT NULL DEFAULT 0.00,
    version         INT             NOT NULL DEFAULT 1,
    operation_id    DECIMAL(15, 2)  NOT NULL DEFAULT 0.00,
    created_at      timestamptz     DEFAULT current_timestamp,
    FOREIGN KEY (balance_id) REFERENCES balances(id)
);

comment on table  balance_audit                  is 'Баланс - аудит';
comment on column balance_audit.id               is 'Уникальный идентификатор записи аудита баланса';
comment on column balance_audit.balance_id       is 'Уникальный идентификатор записи баланса';
comment on column balance_audit.account_id       is 'Номер счета';
comment on column balance_audit.auth_balance     is 'Авторизационная сумма';
comment on column balance_audit.actual_balance   is 'Фактическая сумма';
comment on column balance_audit.version          is 'Версия баланса счета';
comment on column balance_audit.operation_id     is 'id операции, которая изменила баланс';
comment on column balance_audit.created_at       is 'Дата записи аудита';

CREATE INDEX IF NOT EXISTS idx_balance_audit_balance_id ON balance_audit(balance_id);