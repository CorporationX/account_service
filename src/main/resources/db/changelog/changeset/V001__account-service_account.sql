CREATE TABLE accounts (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    balance BIGINT NOT NULL DEFAULT 0,
    number varchar(20) NOT NULL UNIQUE,
    owner_id bigint NOT NULL,
    owner_type int NOT NULL,
    type int NOT NULL,
    currency int NOT NULL,
    status int NOT NULL DEFAULT 1,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp NOT NULL,
    closed_at timestamptz NULL,
    version int NOT NULL DEFAULT 0
);

CREATE INDEX accounts_number_idx ON accounts(number);
CREATE INDEX accounts_owner_idx ON accounts(owner_type, owner_id);