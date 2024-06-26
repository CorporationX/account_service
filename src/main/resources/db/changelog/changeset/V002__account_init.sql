CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) UNIQUE NOT NULL CHECK (number ~ '^[0-9]{12,20}$' ),
    user_id bigint,
    project_id bigint,
    account_type smallint,
    currency smallint,
    status smallint DEFAULT 0 NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version bigint DEFAULT 1 NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id) ON DELETE CASCADE
);

CREATE INDEX idx_user_id ON account (user_id);
CREATE INDEX idx_project_id ON account (project_id);
