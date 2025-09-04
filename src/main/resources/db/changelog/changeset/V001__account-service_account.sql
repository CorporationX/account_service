CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

COMMENT ON FUNCTION update_updated_at_column() IS 'Automatically updates updated_at column on row modification';

CREATE TABLE account (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    number varchar(20) UNIQUE NOT NULL,
    owner_type varchar(32) NOT NULL,
    owner_id bigint NOT NULL,
    type varchar(64) NOT NULL,
    currency varchar(4) NOT NULL,
    status varchar(32) NOT NULL,
    balance numeric(19, 2) NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL,
    closed_at timestamptz,
    version bigint NOT NULL
);

CREATE INDEX idx_account_owner ON account(owner_type, owner_id);