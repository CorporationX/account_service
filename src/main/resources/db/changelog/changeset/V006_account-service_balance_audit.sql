ALTER TABLE balance
    RENAME COLUMN authorization_Balance TO authorization_balance;

ALTER TABLE balance
    RENAME COLUMN actual_Balance TO actual_balance;

ALTER TABLE balance
    RENAME COLUMN create_at TO created_at;

ALTER TABLE balance
    RENAME COLUMN update_at TO updated_at;

CREATE TABLE IF NOT EXISTS balance_audit
(
    id                      BIGINT      PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number                  VARCHAR(20) NOT NULL UNIQUE CHECK (LENGTH(number) BETWEEN 12 AND 20),
    payment_id              BIGINT      NOT NULL,
    authorization_balance   MONEY,
    actual_balance          MONEY,
    version                 BIGINT      NOT NULL,
    created_at              timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at              timestamptz DEFAULT CURRENT_TIMESTAMP
)