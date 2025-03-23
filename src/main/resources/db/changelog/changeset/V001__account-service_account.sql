CREATE TABLE request (
    idempotency_key UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    request_type VARCHAR(64) NOT NULL,
    lock_value BIGINT NOT NULL,
    is_open BOOLEAN NOT NULL DEFAULT FALSE,
    input_params JSONB NOT NULL,
    request_status VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 0
);

CREATE INDEX idx_request_user_id on request (user_id);

CREATE UNIQUE INDEX idx_request_lock_value_open ON request (lock_value) WHERE is_open = TRUE;
CREATE TABLE IF NOT EXISTS account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE ,
    number varchar(20) UNIQUE NOT NULL CHECK (LENGTH(number) BETWEEN 12 AND 20),
    user_id bigint,
    project_id bigint,
    type varchar(40) NOT NULL,
    currency char(3) NOT NULL,
    status VARCHAR(15) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz DEFAULT NULL,
    version int DEFAULT 1,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id),

    CONSTRAINT owner_check CHECK (
        (user_id IS NOT NULL AND project_id IS NULL) OR
        (user_id IS NULL AND project_id IS NOT NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_account_user_id ON account (user_id);
CREATE INDEX IF NOT EXISTS idx_account_project_id ON account (project_id);
