CREATE TABLE balance_audit
(
    id                    bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    balance_id            bigint  NOT NULL,
    balance_version       bigint  NOT NULL,
    authorization_balance decimal NOT NULL,
    actual_balance        decimal NOT NULL,
    operation_id          bigint  NOT NULL,
    created_at            timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_balance_audit_balance FOREIGN KEY (balance_id) REFERENCES balance (id)

);

CREATE INDEX balance_audit_balance_idx ON balance_audit (balance_id);