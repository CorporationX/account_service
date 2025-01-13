CREATE TABLE account_schema.balance_audit(

    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) NOT NULL,
    version BIGINT DEFAULT 0,
    authorization_balance DECIMAL NOT NULL,
    actual_balance DECIMAL NOT NULL,
    operation_id BIGINT,
    created_at timestamptz DEFAULT current_timestamp NOT NULL

--     CONSTRAINT fk_account_number FOREIGN KEY (account_number) REFERENCES account_schema.account (payment_number)
);

CREATE INDEX account_number_idx ON account_schema.balance_audit (account_number);