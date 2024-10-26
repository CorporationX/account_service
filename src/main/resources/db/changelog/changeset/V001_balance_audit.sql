CREATE TABLE IF NOT EXISTS balance_audit
(
    id                            BIGSERIAL PRIMARY KEY,
    balance_id                    BIGINT REFERENCES balance (id) UNIQUE,
    balance_version               BIGINT      NOT NULL,
    current_authorization_balance DECIMAL(18, 2),
    current_actual_balance        DECIMAL(18, 2),
    operation_type                  VARCHAR      NOT NULL,
    audit_at                      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_operation_type ON balance_audit (operation_type);
CREATE INDEX idx_balance_id ON balance_audit (balance_id);
