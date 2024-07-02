CREATE TABLE IF NOT EXISTS transaction
(
    id                      bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    idempotency_key         UUID           NOT NULL UNIQUE,
    sender_account_number   VARCHAR(255)   NOT NULL,
    receiver_account_number VARCHAR(255)   NOT NULL,
    currency                VARCHAR(10)    NOT NULL,
    amount                  NUMERIC(15, 2) NOT NULL,
    transaction_status      VARCHAR(255)   NOT NULL,
    scheduled_at            timestamptz    NOT NULL,
    created_at              timestamptz    NOT NULL
);