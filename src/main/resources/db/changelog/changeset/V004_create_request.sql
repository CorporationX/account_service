CREATE TABLE IF NOT EXISTS request (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    lock_value VARCHAR(100),
    is_open BOOLEAN NOT NULL DEFAULT TRUE,
    input_data JSONB,
    status VARCHAR(50) NOT NULL,
    status_details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT chk_request_type CHECK (request_type IN ('AUTHORIZATION','CANCEL','CLEARING')),
    CONSTRAINT chk_status CHECK (status IN ('PENDING','AUTHORIZED','CLEARED','CANCELED','FAILED'))
    );