CREATE TABLE balance
(
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_id bigint NOT NULL,
    current_authorization_balance bigint,
    current_actual_balance bigint,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    version bigint NOT NULL,

    CONSTRAINT fk_account_id FOREIGN KEY (account_id) REFERENCES account (id)
)