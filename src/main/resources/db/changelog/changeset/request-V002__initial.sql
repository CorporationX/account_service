CREATE TABLE request (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    idempotency_token UUID NOT NULL UNIQUE,
    payment_account_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(64) NOT NULL,
    is_open BOOLEAN DEFAULT TRUE NOT NULL,
    input JSONB,
    request_status VARCHAR(64) NOT NULL DEFAULT 'TODO',
    status_note VARCHAR(4096),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version INTEGER
);

CREATE UNIQUE INDEX idx_request_open_lock
    ON request (payment_account_id)
    WHERE is_open = TRUE AND request_status = 'IN_PROGRESS';

CREATE INDEX idx_request_status_open
    ON request (request_status, request_type, is_open, created_at)
    WHERE request_status = 'TODO' AND is_open = TRUE;

