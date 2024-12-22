ALTER TABLE balance_audit ALTER COLUMN version TYPE INTEGER;

DROP INDEX IF EXISTS account_hash_index;
CREATE INDEX idx_account_created_at_index ON balance_audit (account_id, created_at);

CREATE OR REPLACE FUNCTION update_version_audit()
RETURNS TRIGGER AS $$
BEGIN
    NEW.version := COALESCE(
        (SELECT MAX(version) FROM balance_audit WHERE account_id = NEW.account_id), 0) + 1;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_version
    BEFORE INSERT ON balance_audit
    FOR EACH ROW
    EXECUTE FUNCTION update_version_audit();