CREATE SEQUENCE number_id_seq START 1;
CREATE TABLE free_account_number (
    id BIGINT PRIMARY KEY DEFAULT nextval('number_id_seq'),
    number VARCHAR(20) NOT NULL,
    type VARCHAR(32) NOT NULL
);