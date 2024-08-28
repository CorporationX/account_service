-- Write your sql migration here!
CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number BIGINT NOT NULL UNIQUE CHECK (number > 0),
    owner_type VARCHAR(10) NOT NULL,
    owner_project_id BIGINT,
    owner_user_id BIGINT,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(5) NOT NULL,
    account_status  VARCHAR(10) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    closed_at timestamptz,
    version BIGINT NOT NULL
    );

