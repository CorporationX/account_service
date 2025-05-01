CREATE TABLE account_operations(
    id UUID PRIMARY KEY,
    payment_operation_id UUID NOT NULL,
    sender_account_id UUID NOT NULL,
    recipient_account_id UUID NOT NULL,
    amount DECIMAL(19,4),
    authorization_id UUID,

    currency_code VARCHAR(3) NOT NULL
    CHECK(currency_code IN ('USD', 'EUR')),

    operation_type VARCHAR(20) NOT NULL
    CHECK(operation_type IN ('AUTHORIZATION', 'CLEARING', 'CANCELLATION')),

    operation_status VARCHAR(20) NOT NULL
    CHECK(operation_status IN ('PENDING', 'COMPLETED', 'FAILED')),

    error_message VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE balance(
    account_id UUID PRIMARY KEY,
    auth_balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    clear_balance DECIMAL(19,4) NOT NULL DEFAULT 0,
    currency_code VARCHAR(3) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE balance_audit(
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    payment_operation_id UUID NOT NULL,
    auth_balance_change DECIMAL(19,4) NOT NULL,
    clear_balance_change DECIMAL(19,4) NOT NULL,
    currency_code VARCHAR(3) NOT NULL
);