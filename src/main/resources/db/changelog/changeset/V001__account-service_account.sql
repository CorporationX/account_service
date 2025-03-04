-- Write your sql migration here!
CREATE TABLE account (
    id              BIGSERIAL   PRIMARY KEY,
    account_number  varchar(20) UNIQUE NOT NULL CHECK( LENGTH(account_number) BETWEEN 12 AND  20 ),
    owner_id        BIGINT      NOT NULL,
    owner_type      VARCHAR(16) NOT NULL,
    account_type    VARCHAR(64) NOT NULL,
    currency        VARCHAR(16)  NOT NULL,
    account_status  VARCHAR(16) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    closed_at       TIMESTAMP,
    version         INT         NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS account_number_idx ON account(account_number);
CREATE INDEX IF NOT EXISTS account_owner_idx  ON account(owner_id, owner_type);
CREATE INDEX IF NOT EXISTS account_status_idx ON account(account_status);

