CREATE TABLE balance (
    id UUID     PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP NOT NULL,
    account_id UUID NOT NULL UNIQUE,
    authorized_balance NUMERIC(30, 10) NOT NULL DEFAULT 0 CHECK (authorized_balance >= 0),
    balance     NUMERIC(30, 10) NOT NULL CHECK (balance >= 0)

    CONSTRAINT fk_balance_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE authorization_balance (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL,
    balance_id UUID NOT NULL,
    amount NUMERIC(30, 10) NOT NULL CHECK (amount >= 0),
    status VARCHAR(32) NOT NULL,
    expires_at TIMESTAMP NOT NULL

    CONSTRAINT fk_authorization_balance FOREIGN KEY (balance_id) REFERENCES balance(id)
);