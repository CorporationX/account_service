CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_number varchar(20) UNIQUE NOT NULL,
    user_id BIGINT,
    project_id BIGINT,
    type varchar(16) NOT NULL,
    currency varchar(16) NOT NULL,
    status varchar(16) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
    updated_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 1,

    CONSTRAINT check_account_number_length
        CHECK(length(account_number) BETWEEN 12 AND 20),

    CONSTRAINT check_owner_is_present
        CHECK (
            (user_id IS NOT NULL AND project_id IS NULL) OR
            (user_id IS NULL AND project_id IS NOT NULL)
        )
);