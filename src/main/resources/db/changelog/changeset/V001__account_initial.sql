CREATE TABLE accounts
(
    id             SERIAL PRIMARY KEY,
    account_number VARCHAR(64) UNIQUE NOT NULL,
    author_id      bigint,
    project_id     bigint,
    invoice_type   varchar(32)        NOT NULL,
    currency       character(3)       NOT NULL,
    status         varchar(16)        NOT NULL,
    created_at     timestamptz                 DEFAULT CURRENT_TIMESTAMP,
    updated_at     timestamptz                 DEFAULT CURRENT_TIMESTAMP,
    closed_at      timestamptz,
    version        smallint           NOT NULL DEFAULT 0
);