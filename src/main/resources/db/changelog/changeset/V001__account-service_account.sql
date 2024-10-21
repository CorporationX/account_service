CREATE TABLE account
(
    id                BIGSERIAL PRIMARY KEY,
    number            VARCHAR(20) CHECK (length(number) >= 12) UNIQUE NOT NULL,
    holder_user_id    BIGINT,
    holder_project_id BIGINT,
    type              SMALLINT NOT NULL,
    currency          SMALLINT NOT NULL,
    status            SMALLINT NOT NULL        DEFAULT 0,
    version           SMALLINT NOT NULL        DEFAULT 1,
    created_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    closed_at         TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_one_owner CHECK (
        (holder_user_id IS NOT NULL AND holder_project_id IS NULL) OR
        (holder_user_id IS NULL AND holder_project_id IS NOT NULL)
        )
);

CREATE INDEX ON account (holder_user_id);
CREATE INDEX ON account (holder_project_id);