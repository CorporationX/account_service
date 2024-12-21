CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    owner_type smallInt NOT NULL,
    owner_id bigint NOT NULL,
    type smallInt NOT NULL,
    currency smallInt NOT NULL,
    status smallInt NOT NULL DEFAULT 0,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    version bigint DEFAULT 1
);

CREATE INDEX idx_account_owner_id ON account (owner_id);
