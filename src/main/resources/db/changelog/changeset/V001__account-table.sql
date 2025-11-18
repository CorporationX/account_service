CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE account
(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_number VARCHAR(20) UNIQUE NOT NULL,
    user_id BIGINT,
    project_id BIGINT,
    type VARCHAR(16) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT check_account_number_length
        CHECK (length(account_number) BETWEEN 12 AND 20),

    CONSTRAINT check_owner_is_present
        CHECK (
            (user_id IS NOT NULL AND project_id IS NULL) OR
            (user_id IS NULL AND project_id IS NOT NULL)
            )
);
