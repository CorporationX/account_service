CREATE TABLE free_account_numbers (
                                      account_type VARCHAR(50) NOT NULL,
                                      account_number VARCHAR(20) NOT NULL CHECK (account_number ~ '^\d{12,20}$'),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT pk_free_account_numbers PRIMARY KEY (account_type, account_number)
);

CREATE INDEX idx_free_account_numbers_type ON free_account_numbers(account_type);

COMMENT ON TABLE free_account_numbers IS 'Таблица для хранения свободных номеров счетов';
COMMENT ON COLUMN free_account_numbers.account_type IS 'Тип счета';
COMMENT ON COLUMN free_account_numbers.account_number IS 'Номер счета';
COMMENT ON COLUMN free_account_numbers.created_at IS 'Дата создания записи';