CREATE TABLE IF NOT EXISTS accounts_users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number INT(20) UNIQUE NOT NULL CHECK account_number>=12,
    owner VARCHAR DEFAULT user,
    owner_id BIGINT NOT NULL,
    type VARCHAR(128),
    currency VARCHAR NOT NULL,
    status VARCHAR DEFAULT active,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version VARCHAR NOT NULL,
);

CREATE TABLE IF NOT EXISTS accounts_projects (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number INT(20) UNIQUE NOT NULL CHECK account_number>=12,
    owner VARCHAR DEFAULT project,
    owner_id BIGINT NOT NULL,
    type VARCHAR(128),
    currency VARCHAR NOT NULL,
    status VARCHAR DEFAULT active,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version VARCHAR(20) NOT NULL,
);

CREATE UNIQUE INDEX owner_id_project_idx ON accounts_projects (owner_id);

CREATE UNIQUE INDEX owner_id_user_idx ON accounts_users (owner_id);
