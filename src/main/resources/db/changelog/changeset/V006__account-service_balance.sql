CREATE TABLE IF NOT EXISTS balance
(
    id                    BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id            BIGINT  NOT NULL,
    authorization_balance DECIMAL NOT NULL,
    actual_balance        DECIMAL NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    version               INT     NOT NULL DEFAULT 1,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS account_idx ON balance (account_id);