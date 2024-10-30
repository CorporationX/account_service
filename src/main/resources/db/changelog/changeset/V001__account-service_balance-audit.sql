CREATE TABLE balance_audit (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL CHECK (LENGTH(number) BETWEEN 12 AND 20),
    version BIGINT NOT NULL,
    authorized_balance numeric(10, 2)NOT NULL,
    actual_balance numeric(10, 2)NOT NULL,
    operation_id BIGINT NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);
