CREATE TABLE owners (
    id serial primary key,
    owner_type varchar(32) not null,
    account_id bigint references accounts(id)
);
