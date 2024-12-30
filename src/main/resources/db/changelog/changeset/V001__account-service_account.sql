CREATE TYPE status AS ENUM ('active', 'frozen', 'closed');
CREATE TYPE owner AS ENUM ('project', 'user');

CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) UNIQUE NOT NULL,
    owner_account OWNER,
    user_owner_id BIGINT,
    project_owner_id BIGINT,
    type VARCHAR(128),
    currency VARCHAR(8) NOT NULL,
    current_status STATUS,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMPTZ,
    version BIGINT NOT NULL,

    CONSTRAINT check_number CHECK (number LIKE '^[0-9]{12-20}$'),
    CONSTRAINT check_user_owner_id CHECK (user_owner_id = NULL AND owner_account = 'project'),
    CONSTRAINT check_project_owner_id CHECK (project_owner_id = NULL AND owner_account = 'user')
);

CREATE INDEX idx_user_owner_id ON account (user_owner_id);
CREATE INDEX idx_project_owner_id ON account (project_owner_id);
