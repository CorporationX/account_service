DROP TABLE balance_audit;
DROP TABLE balance;
DROP TABLE account;
DROP TABLE pending_operations;

DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V001__account-service_account.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V002__account-service_balance.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V001__account-service_balance-audit.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V003__account-service_pending_operations.sql';