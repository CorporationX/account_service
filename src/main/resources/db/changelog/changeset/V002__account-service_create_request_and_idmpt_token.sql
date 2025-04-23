CREATE TABLE IF NOT EXISTS request (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         request_type VARCHAR(50),
                         lock_value VARCHAR(255),
                         is_open BOOLEAN DEFAULT TRUE,
                         input_data JSONB,
                         status VARCHAR(50),
                         status_details TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         version INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS idempotency_token (
                                   token UUID PRIMARY KEY,
                                   request_id BIGINT NOT NULL UNIQUE,
                                   FOREIGN KEY (request_id) REFERENCES request(id)
);