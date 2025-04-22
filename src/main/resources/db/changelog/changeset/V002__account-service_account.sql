CREATE TABLE IF NOT EXISTS account
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_number   VARCHAR(20)    NOT NULL UNIQUE,
    owner_id         BIGINT      NOT NULL,
    owner_type       VARCHAR(50) NOT NULL,
    account_type     VARCHAR(50) NOT NULL,
    account_currency CHAR(3)     NOT NULL,
    account_status   VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at        TIMESTAMPTZ          DEFAULT NULL,
    version          INT         NOT NULL DEFAULT 0,
    CHECK (CHAR_LENGTH(account_number) BETWEEN 12 AND 20)
    );

CREATE INDEX IF NOT EXISTS idx_owner ON account (owner_id, owner_type);
CREATE INDEX IF NOT EXISTS idx_account_number ON account (account_number);