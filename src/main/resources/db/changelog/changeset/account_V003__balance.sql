CREATE TABLE IF NOT EXISTS balance
(
    id BIGSERIAL PRIMARY KEY,
    account_id BIGSERIAL NOT NULL UNIQUE,
    authorized_value DECIMAL NOT NULL DEFAULT 0,
    actual_value DECIMAL NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE INDEX idx_account_id ON balance (account_id);