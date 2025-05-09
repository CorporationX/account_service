CREATE TABLE IF NOT EXISTS request (
    idempotency_token UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    lock_value VARCHAR(255),
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    input JSONB,
    status VARCHAR(50) NOT NULL,
    status_details TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version INT DEFAULT 0 NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_request_user_id ON request(user_id);

CREATE UNIQUE INDEX IF NOT EXISTS uniq_request_lock_open
    ON request(lock_value)
    WHERE is_open = true;
