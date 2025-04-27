CREATE TABLE payment_account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT,
    project_id BIGINT,
    account_type VARCHAR(50) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP WITHOUT TIME ZONE,
    version BIGINT NOT NULL DEFAULT 0,

     CONSTRAINT chk_account_number_format CHECK (
            account_number ~ '^\d{12,20}$'
        )
);

CREATE INDEX idx_payment_account_owner ON payment_account (account_number);
CREATE INDEX idx_payment_account_status ON payment_account (status);
CREATE INDEX idx_payment_account_created_at ON payment_account (created_at);