CREATE TABLE account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(20) NOT NULL UNIQUE,
    owner_type VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    currency VARCHAR(20) NOT NULL,
    account_status VARCHAR(20) NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    close_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
    );

CREATE INDEX idx_number ON account (number);
CREATE INDEX idx_owner_id ON account (owner_id, owner_type);