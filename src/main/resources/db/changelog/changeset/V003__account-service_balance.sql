CREATE TABLE balance (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    account_id bigint NOT NULL,
    authorisation_balance VARCHAR(64) NOT NULL,
    actual_balance VARCHAR(64) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    version bigint,
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES account (id)
);
