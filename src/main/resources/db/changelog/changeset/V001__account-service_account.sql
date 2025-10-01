CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS account
(
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4() UNIQUE,
    number VARCHAR(20) NOT NULL UNIQUE,
    user_id bigint,
    project_id bigint,
    account_type smallint NOT NULL,
    currency CHAR(3) NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version int DEFAULT 0 NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
--     CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE,
    CONSTRAINT check_min_number_length CHECK (LENGTH(number) >= 12),
    CONSTRAINT check_owner_is_present
        CHECK (
            (user_id IS NOT NULL AND project_id IS NULL) OR
            (user_id IS NULL AND project_id IS NOT NULL)
        )
);

CREATE INDEX IF NOT EXISTS account_user_id_idx ON account(user_id);
-- CREATE INDEX IF NOT EXISTS account_project_id_idx ON account(project_id);
CREATE INDEX IF NOT EXISTS account_number_idx ON account(number);
