CREATE TABLE IF NOT EXIST balance_audit (
    id bigserial PRIMARY KEY,
    account_id BIGINT NOT NULL,
    version INTEGER NOT NULL,
    authorization_balance DECIMAL(19, 4) NOT NULL,
    actual_balance DECIMAL(19, 4) NOT NULL,
    operation_id BIGINT NOT NULL,
    created_at timestamptz NOT NULL DEFAULT current_timestamp

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES balance (account_id)
)

CREATE INDEX idx_balance_audit_account_id ON balance_audit (account_id);

CREATE INDEX idx_balance_audit_operation_id ON balance_audit (operation_id);