CREATE TYPE account_status AS ENUM ('ACTIVE', 'CLOSED', 'FROZEN');

CREATE TABLE IF NOT EXISTS account
(
    id SERIAL PRIMARY KEY,
    number BIGINT NOT NULL,
    user_id BIGINT REFERENCES users(id),
    project_id BIGINT REFERENCES project(id),
    type VARCHAR(255) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    status account_status,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    closed_at TIMESTAMP WITH TIME ZONE,
    version INTEGER
);

CREATE INDEX IF NOT EXISTS idx_account_user_id ON account (user_id);
CREATE INDEX IF NOT EXISTS idx_account_project_id ON account (project_id);

ALTER TABLE account
ADD CONSTRAINT owner_check CHECK (
        (user_id IS NOT NULL AND project_id IS NULL) OR
        (user_id IS NULL AND project_id IS NOT NULL)
    );