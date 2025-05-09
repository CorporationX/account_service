CREATE TABLE IF NOT EXISTS request
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT,
    request_type   VARCHAR(255),
    lock_value     BIGINT,
    is_open        BOOLEAN,
    input_data     JSONB,
    status         VARCHAR(255),
    status_details TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version        INTEGER
);

CREATE TABLE IF NOT EXISTS idempotency_token
(
    token      UUID PRIMARY KEY,
    request_id BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_token_request FOREIGN KEY (request_id) REFERENCES request (id)
);