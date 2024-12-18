DO
$$
    BEGIN
        IF EXISTS(SELECT 1 FROM pg_catalog.pg_constraint WHERE conname = 'owner_owner_id_key') THEN
            ALTER TABLE owner
                DROP CONSTRAINT owner_owner_id_key;
            CREATE INDEX idx_owner_id_type ON owner (owner_id, type);
        END IF;
    end;
$$

