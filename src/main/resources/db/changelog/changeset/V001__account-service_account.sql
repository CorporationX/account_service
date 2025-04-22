-- Write your sql migration here!
CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS as IDENTITY UNIQUE,
    number varchar(20) NOT NULL UNIQUE CHECK(LENGTH(number) BETWEEN 12 AND 20),
    owner_type varchar(32) NOT NULL,
    owner_id bigint NOT NULL,
    type varchar(32) NOT NULL,
    currency varchar(3) NOT NULL,
    status varchar(32) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamp,
    version int DEFAULT 0
);

CREATE INDEX IF NOT EXISTS owner_type_and_id_idx ON account(owner_type, owner_id);
CREATE INDEX IF NOT EXISTS number_idx ON account(number);