DROP INDEX IF EXISTS account_user_project;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS balance;
DROP INDEX IF EXISTS account_idx;
DROP INDEX IF EXISTS account_number;
DROP TABLE IF EXISTS payment_request;


DELETE
FROM databasechangelog;