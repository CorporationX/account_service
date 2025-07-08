CREATE TABLE account_numbers_sequence (
                                          account_type VARCHAR(50) NOT NULL,
                                          current_value BIGINT NOT NULL DEFAULT 0,
                                          version BIGINT NOT NULL DEFAULT 0,
                                          updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,

                                          CONSTRAINT pk_account_numbers_sequence PRIMARY KEY (account_type)
);

COMMENT ON TABLE account_numbers_sequence IS 'Таблица для хранения счетчиков номеров счетов';
COMMENT ON COLUMN account_numbers_sequence.account_type IS 'Тип счета';
COMMENT ON COLUMN account_numbers_sequence.current_value IS 'Текущее значение счетчика';
COMMENT ON COLUMN account_numbers_sequence.version IS 'Версия для optimistic locking';
COMMENT ON COLUMN account_numbers_sequence.updated_at IS 'Дата последнего обновления';