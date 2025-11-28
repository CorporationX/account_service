CREATE TABLE balances (
                        id bigint               PRIMARY KEY     GENERATED ALWAYS AS IDENTITY,
                        account_id bigint                       NOT NULL,
                        authorization_balance   numeric(19, 2)  NOT NULL DEFAULT 0.00,
                        actual_balance          numeric(19, 2)  NOT NULL DEFAULT 0.00,
                        created_at              timestamptz     DEFAULT current_timestamp,
                        updated_at              timestamptz     DEFAULT current_timestamp,
                        version                 int             NOT NULL DEFAULT 0,

                        CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_account_id ON balances(account_id);

COMMENT ON TABLE    balances                        IS 'Баланс счетов';
COMMENT ON COLUMN   balances.account_id             IS 'Ссылка на счет';
COMMENT ON COLUMN   balances.authorization_balance  IS 'Текущий авторизационный баланс';
COMMENT ON COLUMN   balances.actual_balance         IS 'Текущий фактический баланс';
COMMENT ON COLUMN   balances.version                IS 'Версия для Optimistic Locking';