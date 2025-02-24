CREATE TABLE balance (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id bigint NOT NULL,
    authorized_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    actual_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version bigint NOT NULL DEFAULT 1,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE,

    CHECK (authorized_balance >= 0),
    CHECK (actual_balance >= 0),
    CHECK (version > 0)
);