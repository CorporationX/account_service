ALTER TABLE owner DROP CONSTRAINT owner_owner_id_key;

CREATE INDEX idx_owner_id_type ON owner (owner_id, type);