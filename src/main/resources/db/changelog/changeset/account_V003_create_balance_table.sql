CREATE SEQUENCE sequence_balance_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE balance
(
    id                 BIGINT PRIMARY KEY DEFAULT nextval('sequence_balance_id'),
    account_id         BIGINT         NOT NULL UNIQUE,
    authorized_balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    actual_balance     DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version            INTEGER        NOT NULL DEFAULT 1,
    FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE,
    CONSTRAINT positive_version CHECK (version >= 0)
);

CREATE INDEX idx_balance_account_id ON balance(account_id);