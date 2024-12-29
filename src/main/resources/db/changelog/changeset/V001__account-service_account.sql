CREATE TYPE status AS ENUM ('active', 'frozen', 'closed');
CREATE TYPE owner AS ENUM ('project', 'user');

CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) UNIQUE NOT NULL,
    owner_account OWNER,
    user_owner_id BIGINT,
    project_owner_id BIGINT,
    type VARCHAR(128),
    amount MONEY DEFAULT 0,
    currency VARCHAR(8) NOT NULL COLLATE latin1_general_cs_as,
    current_status STATUS,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version VARCHAR(8) NOT NULL,

    CONSTRAINT check_number CHECK (REGEXP_LIKE(number, '^[0-9]{12-20}$')),
    CONSTRAINT check_user_owner_id CHECK (user_owner_id NULLIF (owner_account, 'project')),
    CONSTRAINT check_project_owner_id CHECK (project_owner_id NULLIF (owner_account, 'user')),
);

CREATE UNIQUE INDEX idx_owner_id_account ON account (user_owner_id, project_owner_id);
