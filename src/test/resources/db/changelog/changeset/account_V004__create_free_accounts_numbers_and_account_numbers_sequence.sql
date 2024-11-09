create table free_account_numbers (
    account_number varchar(20) not null primary key,
    account_type varchar(8) not null
);

create table account_numbers_sequence (
    account_type varchar(8) not null primary key,
    current_number bigint not null default 0
);

insert into account_numbers_sequence (account_type)
values ('5536'),
       ('4276'),
       ('5200');
