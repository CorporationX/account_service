CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    owner_user_id BIGINT,
    owner_project_id BIGINT,
    type VARCHAR(128) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version BIGINT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS account_user_project ON account (owner_user_id, owner_project_id);
