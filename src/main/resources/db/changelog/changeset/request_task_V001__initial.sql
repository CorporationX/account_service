CREATE TABLE if not exists request_task
(
    request_id uuid        not null,
    handler    VARCHAR(32) not null,
    status     VARCHAR(32) not null,
    created_at TIMESTAMPTZ          DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    version    BIGINT      not null default 0
)