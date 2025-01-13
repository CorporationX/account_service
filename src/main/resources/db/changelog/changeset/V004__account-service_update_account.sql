ALTER TABLE balance
    DROP CONSTRAINT IF EXISTS balance_fk;

ALTER TABLE account
    DROP CONSTRAINT IF EXISTS check_number;

ALTER TABLE account
    DROP CONSTRAINT IF EXISTS check_user_owner_id;

ALTER TABLE account
    DROP CONSTRAINT IF EXISTS check_project_owner_id;

ALTER TABLE account
     ALTER COLUMN current_status TYPE VARCHAR(32);

ALTER TABLE account
    ALTER COLUMN owner_account TYPE VARCHAR(32) USING owner_account::TEXT;

DROP TYPE IF EXISTS status;
DROP TYPE IF EXISTS owner;

ALTER TABLE account
    ADD CONSTRAINT check_number CHECK (number::TEXT ~ '^[[:digit:]]{12,20}$');
ALTER TABLE account
    ADD CONSTRAINT check_user_owner_id CHECK (
    (user_owner_id IS NULL AND owner_account = 'PROJECT') OR
    (user_owner_id IS NOT NULL AND owner_account = 'USER')
);

ALTER TABLE account
    ALTER COLUMN number TYPE BIGINT USING number::BIGINT;

ALTER TABLE account
    ALTER COLUMN currency TYPE VARCHAR(32);

ALTER TABLE balance
    ALTER COLUMN account_number TYPE BIGINT USING account_number::BIGINT;

ALTER TABLE balance
    ADD CONSTRAINT balance_fk FOREIGN KEY(account_number) REFERENCES account(number);