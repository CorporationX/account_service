CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number VARCHAR(32),
    owner VARCHAR(32),
    account_type VARCHAR(32),
    currency VARCHAR(32),
    amount bigint
);
