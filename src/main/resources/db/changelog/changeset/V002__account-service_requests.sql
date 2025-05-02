CREATE TABLE IF NOT EXISTS requests(
    idempotency_token UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(32) NOT NULL,
    lock_id BIGINT NOT NULL,
    is_open BOOLEAN NOT NULL,
    input_data VARCHAR(1024) NOT NULL,
    request_status VARCHAR(32) NOT NULL,
    status_details VARCHAR(256),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_requests_user_id ON requests(user_id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_open_lock_id ON requests(lock_id)
    WHERE is_open = true;
