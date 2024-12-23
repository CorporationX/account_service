CREATE TABLE IF NOT EXISTS request
(
    id uuid NOT NULL UNIQUE PRIMARY KEY
);

ALTER TABLE request
    ADD COLUMN IF NOT EXISTS context      VARCHAR(256),
    ADD COLUMN IF NOT EXISTS scheduled_at TIMESTAMP;

CREATE TABLE IF NOT EXISTS request_task
(
    id         uuid NOT NULL PRIMARY KEY,
    handler    VARCHAR(64),
    status     VARCHAR(64) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version    BIGINT DEFAULT 1
);