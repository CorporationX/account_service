CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL CHECK (LENGTH(number) BETWEEN 12 AND 20),
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    balance BIGINT NOT NULL,
    currency VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 1
);
CREATE INDEX project_id_index ON account (project_id);
CREATE INDEX user_id_index ON account (user_id);
