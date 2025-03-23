CREATE TABLE request (
    idempotency_key UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(64) NOT NULL,
    lock_value BIGINT NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT FALSE,
    input_params JSONB NOT NULL,
    request_status VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_request_user_id on request (user_id);

CREATE UNIQUE INDEX idx_request_lock_value_open ON request (lock_value) WHERE is_open = TRUE;