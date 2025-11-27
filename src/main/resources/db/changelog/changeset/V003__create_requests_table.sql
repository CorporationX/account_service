CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE requests
(
    idempotency_token UUID PRIMARY KEY,
    user_id BIGINT,
    project_id BIGINT,
    operation_type VARCHAR(32) NOT NULL,
    lock_value VARCHAR(255) NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT true,
    input_data JSONB NOT NULL,
    request_status VARCHAR(32) NOT NULL,
    status_details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT check_owner_is_present
        CHECK (
            (user_id IS NOT NULL AND project_id IS NULL) OR
            (user_id IS NULL AND project_id IS NOT NULL)
            )
);

CREATE INDEX idx_requests_user_id ON requests(user_id) WHERE user_id IS NOT NULL;
CREATE INDEX idx_requests_project_id ON requests(project_id) WHERE project_id IS NOT NULL;

CREATE UNIQUE INDEX idx_requests_lock_open
    ON requests(lock_value) WHERE is_open = true;