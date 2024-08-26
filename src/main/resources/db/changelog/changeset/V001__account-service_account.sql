CREATE TABLE account
(
    id             BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    number         VARCHAR(20) NOT NULL CHECK (number ~ '^[0-9]{12,20}$'),
    user_id        BIGSERIAL,
    project_id     BIGSERIAL,
    account_type   VARCHAR(30) NOT NULL,
    currency       VARCHAR(10) NOT NULL,
    account_status VARCHAR(15) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP,
    closed_at      TIMESTAMP,
    version        INT NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE INDEX idx_user ON account (user_id);