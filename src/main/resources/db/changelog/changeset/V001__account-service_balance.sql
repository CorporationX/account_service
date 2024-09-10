alter table balance
    add column authorization_balance DECIMAL(19, 4) DEFAULT 0.0;

alter table balance
    add column balance DECIMAL(19, 4) DEFAULT 0.0;

alter table balance
    add column created_at timestamptz DEFAULT current_timestamp;

alter table balance
    add column updated_at timestamptz DEFAULT current_timestamp;

alter table balance
    add column version bigint NOT NULL default 0;

alter table balance
    add constraint fk_account_id foreign key (account_id)
        references account (id);


alter table account
    add constraint fk_balance_id foreign key (balance_id)
        references balance (id);

