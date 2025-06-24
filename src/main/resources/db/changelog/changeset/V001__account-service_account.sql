CREATE TABLE accounts (
    id bigint primary key,
    balance DECIMAL(19, 4) NOT NULL DEFAULT 0.0,
    owner VARCHAR(16) NOT NULL,
    owner_id bigint NOT NULL,
    type VARCHAR(32) NOT NULL,
    currency VARCHAR(16) NOT NULL,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version VARCHAR(16)
);

CREATE INDEX owner_idx ON accounts(owner_id)