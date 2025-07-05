CREATE SEQUENCE account_number_seq START 1;
CREATE TABLE account_number_sequence (
    id BIGINT PRIMARY KEY DEFAULT nextval('account_number_seq'),
    number VARCHAR(20) NOT NULL,
    type VARCHAR(32) NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL
);

INSERT INTO account_number_sequence (number, type) VALUES
('420000000000', 'CURRENT'),
('520000000000', 'SETTLEMENT'),
('770000000000', 'CREDIT'),
('110000000000', 'DEPOSIT'),
('330000000000', 'BUDGET');