-- Создаем последовательность для таблицы request
CREATE SEQUENCE request_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE request (
    id BIGINT PRIMARY KEY DEFAULT nextval('request_id_seq'), -- Идентификатор с использованием sequence
    idempotency_token UUID,
    account_id BIGINT NOT NULL REFERENCES account(id),
    request_type VARCHAR(50) NOT NULL,
    operation_type VARCHAR(50),
    input_data JSONB,
    status VARCHAR(50) NOT NULL,
    status_details VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1,

    -- Индексы
    CREATE INDEX idx_request_account_id ON request(account_id),
    CREATE INDEX idx_request_idempotency_token ON request(idempotency_token),
    CREATE UNIQUE INDEX unique_in_progress_request_for_account
    ON request(account_id)
    WHERE status = 'IN_PROGRESS',

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
