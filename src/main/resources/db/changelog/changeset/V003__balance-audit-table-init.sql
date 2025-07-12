CREATE TABLE balance_audit (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number varchar(20) UNIQUE NOT NULL,
    balance_version bigint NOT NULL,
    authorized_amount NUMERIC(30, 10) NOT NULL,
    actual_amount NUMERIC(30, 10) NOT NULL,
    balance_change_id bigint,
    created_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);