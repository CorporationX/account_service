CREATE TABLE request (
    idempotency_token UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    lock_value BIGINT NOT NULL,
    is_open BOOLEAN NOT NULL,
    input_data JSONB,
    status VARCHAR(50) NOT NULL,
    status_details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL
);

CREATE INDEX idx_request_user_id ON request(user_id);

CREATE UNIQUE INDEX idx_request_lock_value_open ON request(lock_value) WHERE is_open = true;

CREATE INDEX idx_request_status ON request(status);