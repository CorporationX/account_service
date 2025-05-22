CREATE TABLE balance_audit (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    balance_version BIGINT NOT NULL,
    authorized_balance NUMERIC(19, 4) NOT NULL,
    actual_balance NUMERIC(19, 4) NOT NULL,
    operation_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_account FOREIGN KEY (account_id)
        REFERENCES account (id) ON DELETE CASCADE
);

CREATE INDEX idx_balance_audit_account_id
    ON balance_audit (account_id);