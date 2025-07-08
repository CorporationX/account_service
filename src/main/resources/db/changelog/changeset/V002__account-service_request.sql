CREATE SEQUENCE requests_id_seq START 1;
CREATE TABLE requests (
    id BIGINT PRIMARY KEY DEFAULT nextval('requests_id_seq'),
    idempotent_token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(32) NOT NULL,
    value_lock VARCHAR(255),
    is_open BOOLEAN NOT NULL DEFAULT FALSE,
    request_input_data JSON,
    status VARCHAR(32) NOT NULL,
    addictional_details VARCHAR(128),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX user_idx ON requests(user_id);

CREATE UNIQUE INDEX open_request_idx ON requests(value_lock)
WHERE is_open AND value_lock IS NOT NULL;

CREATE UNIQUE INDEX idempotent_token_idx ON requests(idempotent_token);