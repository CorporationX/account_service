CREATE TABLE IF NOT EXISTS request
(
    idempotent_token uuid   NOT NULL UNIQUE PRIMARY KEY,
    request_type            VARCHAR(32) NOT NULL,
    request_status          VARCHAR(32) NOT NULL,
    context                 VARCHAR(128),
    scheduled_at            TIMESTAMP
);

ALTER TABLE request
    ADD COLUMN IF NOT EXISTS context      VARCHAR(256),
    ADD COLUMN IF NOT EXISTS scheduled_at TIMESTAMP;