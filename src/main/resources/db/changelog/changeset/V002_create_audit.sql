CREATE TABLE IF NOT EXISTS balance_audit (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    request_id UUID,
    change_amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    old_balance NUMERIC(19,2) NOT NULL,
    new_balance NUMERIC(19,2) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT fk_balance_audit_account FOREIGN KEY(account_id) REFERENCES account_balance(id)
    );