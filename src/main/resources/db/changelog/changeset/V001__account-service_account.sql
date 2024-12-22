CREATE TABLE account
(
    id                BIGSERIAL PRIMARY KEY,
    number            VARCHAR(20) CHECK (length(number) >= 12) UNIQUE NOT NULL,
    user_owner_id     BIGINT,
    project_owner_id  BIGINT,
    type              VARCHAR(32) NOT NULL,
    currency          VARCHAR(3) NOT NULL,
    status            VARCHAR(20) NOT NULL,
    version           BIGINT NOT NULL DEFAULT 1,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    closed_at         TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_one_owner CHECK (
        (user_owner_id IS NOT NULL AND project_owner_id IS NULL) OR
        (user_owner_id IS NULL AND project_owner_id IS NOT NULL)
        ),
    CONSTRAINT fk_user_id FOREIGN KEY (user_owner_id) REFERENCES users (id),
    CONSTRAINT fk_project_id FOREIGN KEY (project_owner_id) REFERENCES project (id)
);

CREATE INDEX ON account (user_owner_id);
CREATE INDEX ON account (project_owner_id);
