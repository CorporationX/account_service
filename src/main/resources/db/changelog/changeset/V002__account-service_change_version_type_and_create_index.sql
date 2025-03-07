-- Changing the account_version type to bigint
ALTER TABLE accounts
ALTER COLUMN account_version TYPE bigint USING account_version::bigint;

--
ALTER TABLE accounts
ADD COLUMN owner_id bigint NOT NULL;

-- Creating index for quick search of accounts of a specific user
CREATE INDEX idx_accounts_owner ON accounts (owner_id);
