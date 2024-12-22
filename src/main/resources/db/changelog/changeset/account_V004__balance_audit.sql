CREATE TABLE IF NOT EXISTS balance_audit (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id BIGINT NOT NULL,
    version INT NOT NULL GENERATED ALWAYS AS IDENTITY UNIQUE,
    authorization_amount BIGINT NOT NULL,
    actual_amount BIGINT NOT NULL,
    operation_id BIGINT NOT NULL,
    created_at TIMESTAMPTZ,

    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE INDEX time_index ON balance_audit (created_at);
CREATE INDEX account_hash_index ON balance_audit USING hash (account_id);