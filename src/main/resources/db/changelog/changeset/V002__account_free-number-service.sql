create table if not exists account_numbers_sequence
(
    counter      bigint                  not null default 0,
    account_type varchar(32) primary key not null
);

create table if not exists free_accounts_numbers
(
    free_number  bigint      not null,
    account_type varchar(32) not null,
    constraint account_type_pk primary key (account_type, free_number)
);
