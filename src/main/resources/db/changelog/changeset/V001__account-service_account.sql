-- Write your sql migration here!
CREATE TABLE request (
    token UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(255) NOT NULL,
    block_value BIGINT NOT NULL,
    is_opened BOOLEAN NOT NULL,
    body JSONB,
    request_status VARCHAR(255) NOT NULL,
    details VARCHAR(255),
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    request_version INT NOT NULL,
    CONSTRAINT request_block_value_unique UNIQUE (block_value)
);

CREATE INDEX idx_request_user_id ON request(user_id);

CREATE INDEX idx_request_created_at ON request(created_at);

CREATE UNIQUE INDEX idx_open_request_block_value
ON request(block_value)
WHERE is_opened = true;

CREATE INDEX idx_open_request_user_block
ON request(user_id, block_value)
WHERE is_opened = true;