-- Создаем последовательность для таблицы request
CREATE SEQUENCE request_id_seq START WITH 1 INCREMENT BY 1;

-- Создаем таблицу request
CREATE TABLE request (
    id BIGINT PRIMARY KEY DEFAULT nextval('request_id_seq'), -- Идентификатор с использованием sequence
    idempotency_token UUID,
    sender_account_id BIGINT NOT NULL REFERENCES account(id),
    recipient_account_id BIGINT NOT NULL REFERENCES account(id),
    request_type VARCHAR(50) NOT NULL,
    operation_type VARCHAR(50),
    input_data JSONB,
    status VARCHAR(50) NOT NULL,
    status_details VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Убираем ON UPDATE CURRENT_TIMESTAMP
    version INTEGER NOT NULL DEFAULT 1,

    -- Constraint для idempotency_token и operation_type
    CONSTRAINT idempotency_token_required CHECK (
        (request_type IN ('TRANSFER_TO_USER', 'TRANSFER_TO_PROJECT') AND idempotency_token IS NOT NULL)
        OR (request_type = 'ACCOUNT_CREATION' AND idempotency_token IS NULL)
    ),
    CONSTRAINT operation_type_required CHECK (
        (request_type IN ('TRANSFER_TO_USER', 'TRANSFER_TO_PROJECT') AND operation_type IS NOT NULL)
        OR (request_type = 'ACCOUNT_CREATION' AND operation_type IS NULL)
    )
);

-- Создаем индексы отдельно
CREATE INDEX idx_request_sender_account_id ON request(sender_account_id);
CREATE INDEX idx_request_idempotency_token ON request(idempotency_token);
CREATE UNIQUE INDEX unique_in_progress_request_for_sender_account
ON request(sender_account_id)
WHERE status = 'IN_PROGRESS';

-- Создаем функцию для обновления поля updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создаем триггер, который вызывает функцию перед каждым обновлением записи
CREATE TRIGGER set_updated_at
BEFORE UPDATE ON request
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();
