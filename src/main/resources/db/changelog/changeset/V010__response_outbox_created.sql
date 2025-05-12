CREATE TABLE IF NOT EXISTS response_outbox (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    idempotency_token UUID NOT NULL UNIQUE,
    event_type varchar(64) NOT NULL,
    sending_status varchar(64) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_response_outbox_status_event_type ON response_outbox (event_type, sending_status);
