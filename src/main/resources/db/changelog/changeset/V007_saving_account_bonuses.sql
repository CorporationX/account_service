ALTER TABLE saving_accounts
ADD COLUMN bonuses INT DEFAULT 0;

ALTER TABLE saving_accounts
ADD COLUMN bonus_updated_at TIMESTAMP;
