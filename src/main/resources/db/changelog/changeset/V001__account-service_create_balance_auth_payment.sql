ALTER TABLE balances
    ALTER COLUMN authorization_balance TYPE BIGINT,
    ALTER COLUMN actual_balance TYPE BIGINT;

CREATE TABLE balance_auth_payment
(
    id         uuid PRIMARY KEY NOT NULL,
    valid_card timestamptz      NOT NULL DEFAULT current_timestamp,
    owner_id   uuid             NOT NULL,
    amount     bigint           NOT NULL,
    created_at timestamp        NOT NULL DEFAULT current_timestamp,
    balance_id uuid             NOT NULL,

    CONSTRAINT fk_auth_payment_balance_id FOREIGN KEY (balance_id) REFERENCES balances (id)
);