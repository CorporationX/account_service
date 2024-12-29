UPDATE balance
SET account_id = id;

ALTER TABLE balance
    DROP CONSTRAINT balance_pkey;

ALTER TABLE balance
    DROP COLUMN id;

ALTER TABLE balance
    ADD CONSTRAINT balance_pkey PRIMARY KEY (account_id);