create table free_account_numbers (
    account_type varchar(64) not null,
    account_number numeric not null,
    constraint pk_free_account_numbers primary key (account_type, account_number)
)