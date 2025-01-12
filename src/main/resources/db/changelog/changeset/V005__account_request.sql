CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE request
(
    id                 UUID                     DEFAULT gen_random_uuid() PRIMARY KEY,
    created_by         BIGINT   NOT NULL,
    type               SMALLINT NOT NULL,
    input_data         jsonb,
    status             SMALLINT NOT NULL        DEFAULT 0,
    status_description VARCHAR(512),
    version            INTEGER  NOT NULL        DEFAULT 1,
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ON request (created_by) WHERE status = 0;
CREATE INDEX idx_request_created_by ON request (created_by);