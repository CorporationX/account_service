--liquibase formatted sql

--changeset trytofixme:create_request_20250702
CREATE TABLE request (
    idempotency_token UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    locked_by BIGINT,
    active BOOLEAN NOT NULL,
    storage JSONB,
    status VARCHAR(50) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version INTEGER NOT NULL
);

--changeset trytofixme:create_idx_request_user_id_20250702
CREATE INDEX idx_request_user_id ON request(user_id);

--changeset trytofixme:create_uq_request_locked_by_open_20250702
CREATE UNIQUE INDEX uq_request_locked_by_open
ON request(locked_by)
WHERE active = true;
