CREATE TABLE IF NOT EXISTS transfer_requests
(
    id                  UUID PRIMARY KEY NOT NULL,
    sender_account_id   BIGINT           NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    receiver_account_id BIGINT           NOT NULL REFERENCES account (id) ON DELETE CASCADE,
    amount              NUMERIC(19, 2)   NOT NULL,
    currency            VARCHAR(255)     NOT NULL,
    auth_payment_id     UUID             NOT NULL REFERENCES auth_payment (id) ON DELETE CASCADE,
    payment_type        VARCHAR(255),
    transfer_status     VARCHAR(255),
    kafka_published     BOOLEAN          NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP
);