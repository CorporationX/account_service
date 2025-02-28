CREATE TABLE IF NOT EXISTS account(
    id bigserial PRIMARY KEY,
    number VARCHAR(20) NOT NULL UNIQUE,
    owner_id bigint NOT NULL,
    owner_type VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL,
    currency VARCHAR(5) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_date TIMESTAMP NOT NULL,
    version bigint NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_account_number ON account(number);
CREATE INDEX IF NOT EXISTS idx_account_owner ON account(owner_id, owner_type);