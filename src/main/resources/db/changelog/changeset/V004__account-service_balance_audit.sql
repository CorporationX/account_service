CREATE SEQUENCE balance_audit_id_seq START 1;
CREATE TABLE balance_audit (
    id BIGINT PRIMARY KEY DEFAULT nextval('balance_audit_id_seq'),
    account_number VARCHAR(20) NOT NULL,
    account_version INTEGER NOT NULL,
    auth_balance DECIMAL(15,2) NOT NULL,
    actual_balance DECIMAL(15,2) NOT NULL,
    operation_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_balance_audit_number ON balance_audit(account_number);



