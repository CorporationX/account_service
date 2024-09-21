CREATE TABLE account
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    number         VARCHAR(20) NOT NULL CHECK ( number ~ '^[0-9]{12,20}$' ),
    account_type   VARCHAR(20) NOT NULL,
    user_id        BIGINT,
    project_id     BIGINT,
    currency       VARCHAR(10) NOT NULL,
    account_status VARCHAR(20) NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at      TIMESTAMPTZ,
    version        INT         NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT chk_user_or_project CHECK (
        (user_id IS NOT NULL AND project_id IS NULL) OR
        (user_id IS NULL AND project_id IS NOT NULL)
        )
);
