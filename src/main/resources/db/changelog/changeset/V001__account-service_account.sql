CREATE TABLE account (
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     varchar(64) UNIQUE NOT NULL,
    project_id bigint,
    user_id    bigint,
    type       varchar(64)   NOT NULL,
    currency   varchar(64)   NOT NULL,
    status     varchar(64)   NOT NULL,
    created_at timestamptz  DEFAULT current_timestamp,
    updated_at timestamptz  DEFAULT current_timestamp,
    closed_at  timestamptz,
    version    bigint       DEFAULT 1,

    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp
BEFORE UPDATE ON account
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();