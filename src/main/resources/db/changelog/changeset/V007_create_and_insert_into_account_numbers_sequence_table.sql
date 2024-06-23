create table account_numbers_sequence (
    id serial primary key,
    account_type varchar(32) not null unique,
    current_number numeric not null default 0,
    version bigint not null default 1
);

insert into account_numbers_sequence (account_type, current_number)
values ('4200', 0), ('5200', 0), ('8800', 0), ('5236', 0), ('2200', 0);