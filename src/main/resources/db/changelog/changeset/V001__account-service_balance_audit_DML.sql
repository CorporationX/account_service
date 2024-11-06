CREATE TABLE balance_audit (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id bigint NOT NULL,
    version bigint NOT NULL,
    payment_number BIGINT,
    authorization_balance DOUBLE PRECISION NOT NULL,
    actual_balance DOUBLE PRECISION NOT NULL,
    operation_id INT NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
);

CREATE INDEX idx_operation_id ON balance_audit (operation_id);

CREATE INDEX idx_account_authorization_balance ON balance_audit (account_id, authorization_balance);