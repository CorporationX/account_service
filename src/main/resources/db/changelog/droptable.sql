DROP TABLE pending_operations;
DROP TABLE pending_operations_account_service;
DROP TABLE balance_audit;
DROP TABLE balance;
DROP TABLE account;


DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V001__account-service_account.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V002__account-service_balance.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V001__account-service_balance-audit.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V003__account-service_pending_operations.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V005__account-service_pending_operations-rename.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V006_account-service_pending_operations-add_key.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V004__account-service_balance-audit_drop-constraint.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/post_V004_pending.sql';
DELETE FROM databasechangelog
WHERE filename = 'db/changelog/changeset/V005__account-service_pending_operations_account_service.sql';
