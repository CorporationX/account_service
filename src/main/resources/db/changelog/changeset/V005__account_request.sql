CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE request
(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_by         BIGINT NOT NULL,
    type               SMALLINT NOT NULL,
	lock_value         BIGINT NOT NULL,
    input_data         JSONB,
    status             SMALLINT NOT NULL DEFAULT 0,
    status_description VARCHAR(512),  
    created_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ON request (created_by) WHERE status = 0;
CREATE INDEX idx_request_created_by ON request (created_by);