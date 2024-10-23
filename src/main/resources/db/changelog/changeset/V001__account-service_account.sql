CREATE TABLE account (
    id SERIAL PRIMARY KEY,
    number VARCHAR(20) NOT NULL,
    owner_id BIGINT NOT NULL,
    owner_type VARCHAR(32) NOT NULL,
    type VARCHAR(32) NOT NULL,
    balance BIGINT NOT NULL,
    currency VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    change_date TIMESTAMP NOT NULL,
    close_date TIMESTAMP,
    version BIGINT NOT NULL
);
CREATE INDEX account_id_index ON account (id);