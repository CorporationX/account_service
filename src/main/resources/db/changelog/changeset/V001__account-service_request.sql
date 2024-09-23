CREATE TABLE requests
(
    id             UUID PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    operation_type VARCHAR(64) NOT NULL,
    lock_request   BOOLEAN      NOT NULL,
    lock_user      BIGINT,
    request_data   JSONB,
    status         VARCHAR(64) NOT NULL,
    description    TEXT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version        BIGINT       NOT NULL
);
