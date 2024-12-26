CREATE TABLE IF NOT EXISTS request
(
    idempotent_token uuid   NOT NULL UNIQUE PRIMARY KEY,
    request_type            VARCHAR(32) NOT NULL,
    request_status          VARCHAR(32) NOT NULL,
    context                 VARCHAR(128),
    scheduled_at            TIMESTAMP
);