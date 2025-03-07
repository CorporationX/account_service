CREATE TABLE requests
(
    idempotent_key      VARCHAR(36) PRIMARY KEY,
    author_account_id   BIGINT      NOT NULL,
    receiver_account_id BIGINT      NOT NULL,
    transaction_type    VARCHAR(32) NOT NULL,
    is_lock             BOOLEAN     NOT NULL DEFAULT FALSE,
    is_open             BOOLEAN     NOT NULL DEFAULT TRUE,
    payload             JSONB       NOT NULL,
    status              VARCHAR(32) NOT NULL,
    status_info         VARCHAR(128),
    version             INTEGER     NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE,
    updated_at          TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT fk_author_account_id FOREIGN KEY (author_account_id) REFERENCES accounts (id),
    CONSTRAINT fk_receiver_account_id FOREIGN KEY (receiver_account_id) REFERENCES accounts (id)
);

CREATE INDEX idx_requests_author_acc_id ON requests (author_account_id);
CREATE UNIQUE INDEX idx_uq_requests_author_acc_id_locked
    ON requests (author_account_id, is_lock)
    WHERE is_lock = TRUE;