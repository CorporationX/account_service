CREATE TABLE request_event (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(255) NOT NULL,
    block_value BIGINT NOT NULL UNIQUE,
    body JSONB DEFAULT '{}',
    request_status VARCHAR(255) NOT NULL,
    details VARCHAR(255),
    request_version INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_request_events_created_at ON request_event(created_at);
