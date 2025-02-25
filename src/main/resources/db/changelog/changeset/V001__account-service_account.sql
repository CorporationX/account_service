-- Write your sql migration here!
DROP TABLE IF EXISTS account;

CREATE TABLE account (
    id              BIGSERIAL   PRIMARY KEY,
    account_number  varchar(20) UNIQUE NOT NULL CHECK( LENGTH(account_number) BETWEEN 12 AND  20 ),
    owner_id        BIGSERIAL   NOT NULL,
    owner_type      VARCHAR(10) NOT NULL,
    account_type    VARCHAR(50) NOT NULL,
    currency        VARCHAR(5)  NOT NULL,
    account_status  VARCHAR(10) NOT NULL,
    created_at      timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamptz,
    closed_at       timestamptz,
    version         INT         NOT NULL DEFAULT 1
);
CREATE INDEX IF NOT EXISTS account_number_idx ON account(account_number);
CREATE INDEX IF NOT EXISTS account_owner_idx  ON account(owner_id, owner_type);
CREATE INDEX IF NOT EXISTS account_status_idx ON account(account_status);

