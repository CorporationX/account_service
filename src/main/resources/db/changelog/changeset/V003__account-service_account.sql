ALTER TABLE ACCOUNT
    ADD CONSTRAINT fk_account_user
        FOREIGN KEY (USER_ID)
            REFERENCES users (ID)
            ON DELETE SET NULL;

ALTER TABLE ACCOUNT
    ADD CONSTRAINT fk_account_project
        FOREIGN KEY (PROJECT_ID)
            REFERENCES project (ID)
            ON DELETE SET NULL;