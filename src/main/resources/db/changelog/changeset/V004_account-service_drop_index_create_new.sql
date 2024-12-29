DROP INDEX IF EXISTS owner_id_idx;

CREATE INDEX IF NOT EXISTS account_idx ON account(owner_type, owner_id);