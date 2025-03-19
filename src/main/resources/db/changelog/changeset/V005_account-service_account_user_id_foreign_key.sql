ALTER TABLE account
    ADD CONSTRAINT fk_account_owner
        FOREIGN KEY (owner_id) REFERENCES users (id)
            ON DELETE CASCADE;
