ALTER TABLE balance_auth_payment
    DROP COLUMN owner_id,
    DROP COLUMN valid_card,
    ADD COLUMN version BIGINT NOT NULL;
