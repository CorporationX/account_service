ALTER TABLE account
ADD CONSTRAINT  fk_user_owner_id FOREIGN KEY (owner_user_id) REFERENCES users (id)  ON DELETE CASCADE;

ALTER TABLE account
ADD CONSTRAINT  fk_project_owner_id FOREIGN KEY (owner_project_id) REFERENCES project (id)  ON DELETE CASCADE;