CREATE SCHEMA IF NOT EXISTS account_schema;

CREATE TABLE account_schema.account
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    payment_number VARCHAR(20) CHECK (payment_number ~ '[0-9]{12,20}$'),
    user_id BIGINT,
    project_id BIGINT,
    balance BIGINT DEFAULT 0,
    type VARCHAR(40) NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    version BIGINT NOT NULL,

    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES project (id)
);
