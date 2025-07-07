CREATE UNIQUE INDEX idx_account_owner_unique ON accounts (owner_id, owner_type);
CREATE UNIQUE INDEX idx_account_number_unique ON accounts (account_number);