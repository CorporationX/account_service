CREATE TABLE IF NOT EXISTS account
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_number   VARCHAR(20)  NOT NULL UNIQUE,
    owner_type       VARCHAR(8)   NOT NULL,
    owner_id         BIGINT       NOT NULL,
    owner_name       VARCHAR(256) NOT NULL,
    account_type     VARCHAR(128) NOT NULL,
    account_currency CHAR(3)      NOT NULL,
    account_status   VARCHAR(32)  NOT NULL,
    created_at       timestamptz           DEFAULT current_timestamp,
    updated_at       timestamptz           DEFAULT current_timestamp,
    deleted_at       timestamptz           DEFAULT NULL,
    account_version  INT          NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS owner_id_idx ON account (owner_id);