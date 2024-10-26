CREATE TABLE pending_operations (
    id bigserial PRIMARY KEY,
    account_id bigserial NOT NULL,
    amount numeric(10, 2),
    created_at timestamp,
    state varchar(20),
    FOREIGN KEY (account_id) REFERENCES account (id) ON DELETE CASCADE
);