ALTER TABLE account
ADD COLUMN owner_id BIGINT;

UPDATE account
SET owner_id = o.id
FROM owner o
WHERE o.account_id = account.id;

ALTER TABLE account
ALTER COLUMN owner_id SET NOT NULL;

ALTER TABLE account
ADD CONSTRAINT fk_account_owner FOREIGN KEY (owner_id) REFERENCES owner(id);

ALTER TABLE owner
DROP COLUMN account_id;

ALTER TABLE owner
ADD CONSTRAINT uk_owner_type_person UNIQUE (owner_type, person_id);

CREATE INDEX IF NOT EXISTS idx_account_owner_id ON account(owner_id);