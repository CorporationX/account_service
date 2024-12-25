CREATE TABLE IF NOT EXISTS request_task
(
    id         BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL,
    handler    VARCHAR(64) NOT NULL,
    status     VARCHAR(32) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version    BIGINT DEFAULT 1
    );