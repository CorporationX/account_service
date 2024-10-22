CREATE TABLE account
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     varchar(64) UNIQUE NOT NULL,
    project_id bigint,
    user_id    bigint,
    type       varchar(64)        NOT NULL,
    currency   varchar(64)        NOT NULL,
    status     varchar(64)        NOT NULL,
    created_at timestamptz                 DEFAULT current_timestamp,
    updated_at timestamptz                 DEFAULT current_timestamp,
    closed_at  timestamptz,
    version    bigint                      DEFAULT 1,

    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_account_user_id ON account (user_id);

CREATE
OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER set_timestamp
    BEFORE UPDATE
    ON account
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE OR REPLACE FUNCTION generate_random_number()
RETURNS text AS $$
BEGIN
RETURN (SELECT string_agg(floor(random() * 10)::int::text, '')
        FROM generate_series(1, trunc(random() * (20 - 12 + 1) + 12)::int));
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION set_account_number()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.number IS NULL THEN
        NEW.number := generate_random_number();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER account_number_trigger
    BEFORE INSERT ON account
    FOR EACH ROW
    EXECUTE FUNCTION set_account_number();