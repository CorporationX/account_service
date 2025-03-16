-- Migration for creating a balance table
CREATE TABLE IF NOT EXISTS balance (
    id                      BIGSERIAL       PRIMARY KEY,
    account_id              BIGINT          NOT NULL REFERENCES account(id) ON DELETE CASCADE,
    auth_balance            NUMERIC(19, 4)  NOT NULL DEFAULT 0,
    actual_balance          NUMERIC(19, 4)  NOT NULL DEFAULT 0,
    created_at              TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP,
    version                 INT             NOT NULL DEFAULT 0
);

-- Index to accelerate the search for account_id
CREATE INDEX IF NOT EXISTS balance_account_id_idx ON balance(account_id);