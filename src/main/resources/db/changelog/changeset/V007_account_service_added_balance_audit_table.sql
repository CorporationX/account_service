CREATE TABLE balance_audit
(
id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
account_id         BIGINT         NOT NULL REFERENCES account(id),
balance_version    INT            NOT NULL,
authorized_balance NUMERIC(18, 2) NOT NULL,
actual_balance     NUMERIC(18, 2) NOT NULL,
transaction_id     BIGINT         NOT NULL REFERENCES transaction(id),
created_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_balance_audit_account_id ON balance_audit (account_id);