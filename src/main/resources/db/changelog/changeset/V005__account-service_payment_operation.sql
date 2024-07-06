CREATE TABLE IF NOT EXISTS payment_operation
(
    id                  UUID PRIMARY KEY,
    sender_account_id   BIGINT      NOT NULL,
    receiver_account_id BIGINT      NOT NULL,
    amount              DECIMAL     NOT NULL,
    currency            VARCHAR(3)  NOT NULL,
    operation_type      VARCHAR(64) NOT NULL,
    operation_status    VARCHAR(16) NOT NULL,
    created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sender_account_number FOREIGN KEY (sender_account_id) REFERENCES account (id),
    CONSTRAINT fk_receiver_account_number FOREIGN KEY (receiver_account_id) REFERENCES account (id)
);