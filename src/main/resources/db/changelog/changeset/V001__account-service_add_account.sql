CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number BIGINT UNIQUE NOT NULL,
    owner_account VARCHAR(128),
    user_owner_id BIGINT,
    project_owner_id BIGINT,
    type VARCHAR(128),
    currency VARCHAR(8) NOT NULL,
    current_status VARCHAR(128),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMPTZ,
    version BIGINT NOT NULL,

    CONSTRAINT check_number CHECK (number::TEXT ~ '^[[:digit:]]{12,20}$')
);

CREATE INDEX idx_user_owner_id ON account (user_owner_id);
CREATE INDEX idx_project_owner_id ON account (project_owner_id);
