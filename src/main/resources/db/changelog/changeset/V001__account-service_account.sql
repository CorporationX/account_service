CREATE TABLE account (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    account_number varchar(20) UNIQUE NOT NULL,
    user_id bigint NOT NULL,
    type varchar(50) NOT NULL,
    currency varchar(3) NOT NULL,
    status varchar(50) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    updated_at timestamptz DEFAULT current_timestamp,
    closed_at timestamptz,
    frozen_at timestamptz,
    blocked_at timestamptz,
    version bigint NOT NULL DEFAULT 0,
    balance numeric(19, 4) NOT NULL DEFAULT 0,
    description varchar(255),
    close_reason varchar(255),
    frozen_reason varchar(255),
    block_reason varchar(255),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_account_user_id ON account (user_id);
