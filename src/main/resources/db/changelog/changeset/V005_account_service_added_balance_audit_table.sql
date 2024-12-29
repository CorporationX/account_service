CREATE TABLE balance_audit
(
id                 BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
account_id         BIGINT         NOT NULL REFERENCES account(id),
balance_version    INT            NOT NULL,
authorized_balance NUMERIC(18, 2) NOT NULL,
actual_balance     NUMERIC(18, 2) NOT NULL,
operation_id       BIGINT         NOT NULL,
created_at         TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
);