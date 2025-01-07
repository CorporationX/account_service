CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE if not exists request_task
(
    id         UUID                 DEFAULT gen_random_uuid() PRIMARY KEY,
    request_id UUID        not null,
    handler    BIGINT      not null,
    status     VARCHAR(32) not null,
    created_at TIMESTAMPTZ          DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    version    BIGINT      not null default 0,

    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES request (id)
);

CREATE INDEX idx_request_id ON request_task (request_id);