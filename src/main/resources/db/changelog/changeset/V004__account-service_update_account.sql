ALTER TABLE balance DROP CONSTRAINT IF EXISTS balance_fk;
DROP TABLE IF EXISTS account;
DROP TYPE IF EXISTS status;
DROP TYPE IF EXISTS owner;

CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number BIGINT UNIQUE NOT NULL,
    owner_account VARCHAR(128),
    user_owner_id BIGINT,
    project_owner_id BIGINT,
    type VARCHAR(128),
    currency VARCHAR(8) NOT NULL,
    current_status VARCHAR(128),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMPTZ,
    version BIGINT NOT NULL,

    CONSTRAINT check_number CHECK (number::TEXT ~ '^[[:digit:]]{12,20}$'),
    CONSTRAINT check_user_owner_id CHECK (
        (user_owner_id IS NULL AND owner_account = 'project') OR
        (user_owner_id IS NOT NULL AND owner_account = 'user'))
);

CREATE INDEX idx_user_owner_id ON account (user_owner_id);
CREATE INDEX idx_project_owner_id ON account (project_owner_id);

ALTER TABLE balance
    ALTER COLUMN account_number TYPE BIGINT USING account_number::BIGINT;
ALTER TABLE balance ADD CONSTRAINT balance_fk FOREIGN KEY(account_number) REFERENCES account(number);