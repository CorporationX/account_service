CREATE TABLE IF NOT EXISTS requests
(
    idempotency_key           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                   BIGINT NOT NULL,
    request_type              VARCHAR(32) NOT NULL CHECK (request_type IN ('IN_PROGRESS', 'BLOCKED', 'DECLINE', 'DONE')),
    lock_key                  VARCHAR(64) NOT NULL,
    is_open                   BOOLEAN NOT NULL DEFAULT TRUE,
    input_request             JSONB NOT NULL,
    request_status            VARCHAR(32) NOT NULL CHECK (request_status IN ('TO_DO', 'WAITING', 'DECLINE', 'DONE')),
    status_details            TEXT,
    created_at                TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version                   INT NOT NULL DEFAULT 0,
    notification_pending      BOOLEAN NOT NULL DEFAULT FALSE,
    pending_notification_type VARCHAR(32)
);

CREATE UNIQUE INDEX IF NOT EXISTS indx_request_lock ON requests(lock_key) where is_open = TRUE;

CREATE INDEX IF NOT EXISTS idx_request_user ON requests(user_id);


