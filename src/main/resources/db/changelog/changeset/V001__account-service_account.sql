CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE accounts (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
                         number VARCHAR(20) NOT NULL UNIQUE,
                         owner_type VARCHAR(10) NOT NULL,
                         owner_id BIGINT NOT NULL,
                         account_type VARCHAR(20) NOT NULL,
                         currency VARCHAR(3) NOT NULL,
                         status VARCHAR(10) NOT NULL,
                         created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                         closed_at TIMESTAMP WITHOUT TIME ZONE,
                         version INTEGER NOT NULL DEFAULT 1
                      );

CREATE INDEX idx_account_owner_type_id ON account (owner_type, owner_id);