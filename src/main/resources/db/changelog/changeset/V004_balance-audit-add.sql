CREATE TABLE balance_audit
(
    id                  BIGSERIAL PRIMARY KEY,
    balance_id          BIGINT                                          NOT NULL,
    number              VARCHAR(20) CHECK (length(number) >= 12) UNIQUE NOT NULL,
    cur_auth_balance    DECIMAL                                         NOT NULL,
    cur_fact_balance    DECIMAL                                         NOT NULL,
    update_operation_id BIGINT                                          NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE                                 DEFAULT CURRENT_TIMESTAMP,
    balance_version     INTEGER                                         NOT NULL DEFAULT 1,
    FOREIGN KEY (balance_id) REFERENCES balance (id)
);
CREATE INDEX idx_balance_id ON balance_audit (balance_id);