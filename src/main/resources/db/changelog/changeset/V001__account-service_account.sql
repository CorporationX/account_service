CREATE TABLE IF NOT EXISTS account_owner (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    owner_id bigint NOT NULL,
    owner_type varchar(16) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    UNIQUE (owner_id, owner_type)
);

CREATE TABLE IF NOT EXISTS account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number varchar(20) NOT NULL UNIQUE,
    owner_id bigint NOT NULL,
    type varchar(32) NOT NULL,
    currency varchar(3) NOT NULL,
    status varchar(16) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL,
    updated_at timestamptz DEFAULT current_timestamp NOT NULL,
    closed_at timestamptz,
    version int DEFAULT 1 NOT NULL,

    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id)
    REFERENCES account_owner (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_account_owner_owner
ON account_owner (owner_id, owner_type);
