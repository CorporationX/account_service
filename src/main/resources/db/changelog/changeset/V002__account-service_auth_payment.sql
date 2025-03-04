CREATE TABLE IF NOT EXISTS auth_payments
(
    id                  UUID PRIMARY KEY NOT NULL,
    sender_account_id   BIGINT           NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    receiver_account_id BIGINT           NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    amount              NUMERIC(19, 2)   NOT NULL,
    currency            VARCHAR(255)     NOT NULL,
    payment_type        VARCHAR(255),
    status              VARCHAR(255),
    created_at          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version             INT              NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS balance
(
    id              UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    account_id      BIGINT         NOT NULL UNIQUE REFERENCES account (id) ON DELETE CASCADE,
    auth_balance    NUMERIC(19, 2) NOT NULL,
    current_balance NUMERIC(19, 2) NOT NULL,
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version         BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_sender_account ON auth_payments (sender_account_id);
CREATE INDEX IF NOT EXISTS idx_receiver_account ON auth_payments (receiver_account_id);

CREATE TABLE IF NOT EXISTS outbox_events
(
    id           UUID PRIMARY KEY NOT NULL,
    class_type   VARCHAR(255)     NOT NULL,
    kafka_topic  VARCHAR(255)     NOT NULL,
    json_payload TEXT             NOT NULL,
    created_at   TIMESTAMP                 DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);