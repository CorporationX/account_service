CREATE TABLE payment_account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    owner_id BIGINT,
    project_id BIGINT,
    account_type SMALLINT DEFAULT 0 NOT NULL,
    currency SMALLINT NOT NULL,
    balance NUMERIC(18, 2) DEFAULT 0.00 NOT NULL,
    status SMALLINT DEFAULT 0 NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp,
    update_at TIMESTAMPTZ DEFAULT current_timestamp,
    closed_at TIMESTAMPTZ DEFAULT NULL,
    version INT DEFAULT 1 NOT NULL,
    CHECK (CHAR_LENGTH(account_number) BETWEEN 12 AND 20),
    CHECK (owner_id IS NOT NULL OR project_id IS NOT NULL)
);

CREATE INDEX idx_payment_account_number ON payment_account (account_number);
CREATE INDEX idx_payment_account_owner_id ON payment_account (owner_id);
CREATE INDEX idx_payment_account_project_id ON payment_account (project_id);
CREATE INDEX idx_payment_account_status ON payment_account (status);
