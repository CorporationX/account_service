-- Write your sql migration here!
CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) NOT NULL UNIQUE CHECK (LENGTH(number) BETWEEN 12 AND 20),
    owner_type VARCHAR(32) NOT NULL,
    owner_project_id BIGINT,
    owner_user_id BIGINT,
    account_type VARCHAR(32) NOT NULL,
    currency VARCHAR(32) NOT NULL,
    account_status  VARCHAR(32) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    closed_at timestamptz,
    version BIGINT NOT NULL
    );

