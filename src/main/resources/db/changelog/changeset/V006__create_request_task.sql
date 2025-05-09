CREATE TABLE IF NOT EXISTS request_task(
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL,
    handler VARCHAR(256) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES request(idempotency_token)
);

CREATE INDEX IF NOT EXISTS idx_request_task_request_id ON request_task(request_id);

