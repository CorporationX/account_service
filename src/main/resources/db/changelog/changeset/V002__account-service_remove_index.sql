DROP INDEX idx_payment_owners_external_type;

ALTER TABLE payment_accounts
ALTER COLUMN id DROP DEFAULT;

ALTER TABLE payment_owners
ALTER COLUMN id DROP DEFAULT;