-- Table for storing pre-generated free account numbers
CREATE TABLE free_account_numbers (
account_type VARCHAR(32) NOT NULL,
account_number BIGINT NOT NULL,

CONSTRAINT free_acc_pk PRIMARY KEY (account_type, account_number)
);

--Table for storing sequence counters per account type
CREATE TABLE account_number_sequence (
account_type VARCHAR(32) NOT NULL PRIMARY KEY,
counter BIGINT NOT NULL DEFAULT 1,
version BIGINT NOT NULL DEFAULT 0
);

--Ensure referential integrity between tables
ALTER TABLE free_account_numbers
ADD CONSTRAINT fk_account_type
FOREIGN KEY (account_type) REFERENCES account_number_sequence(account_type)
ON DELETE CASCADE;

--Index for faster retrieval of free numbers by type
CREATE INDEX idx_free_acc_type ON free_account_numbers(account_type);

--Initialize supported account types
INSERT INTO account_number_sequence (account_type, counter, version) VALUES
('DEBIT', 1, 0),
('CREDIT', 1, 0)
ON CONFLICT (account_type) DO NOTHING;