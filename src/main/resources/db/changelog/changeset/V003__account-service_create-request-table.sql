CREATE TABLE request (
    idempotency_key UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type SMALLINT NOT NULL,
    lock_value BIGINT NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT FALSE,
    input_data JSONB NOT NULL,
    request_status SMALLINT NOT NULL DEFAULT 0,
    status_description VARCHAR(512),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    version INT NOT NULL DEFAULT 1
);

CREATE INDEX idx_request_user_id on request (user_id);

CREATE UNIQUE INDEX idx_request_lock_value_open ON request (lock_value) WHERE is_open = TRUE;