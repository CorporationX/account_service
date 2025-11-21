CREATE TABLE account (
    id                   UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number       VARCHAR(20)    UNIQUE NOT NULL,
    user_id              BIGINT         NOT NULL,
    type                 VARCHAR(64)    NOT NULL,
    currency             VARCHAR(3)     NOT NULL,
    status               VARCHAR(64)    NOT NULL,
    created_at           TIMESTAMPTZ    DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMPTZ    DEFAULT CURRENT_TIMESTAMP,
    version              BIGINT         NOT NULL DEFAULT 0,
    balance              NUMERIC(19, 4) NOT NULL DEFAULT 0,
    description          VARCHAR(255),
    status_change_reason VARCHAR(255),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_account_user_id ON account (user_id);
