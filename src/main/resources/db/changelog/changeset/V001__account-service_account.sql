create table account_type
(
    id   bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    name varchar(128) not null
);

insert into account_type (name)
values ('credit account'),
       ('settlement account'),
       ('fixed-term savings account'),
       ('salary account'),
       ('corporate account');

create table account_numbers_sequence
(
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number       bigint not null,
    account_type_id bigint not null,
    constraint account_type_fk foreign key (account_type_id) references account_type (id) ON DELETE CASCADE
);

create table free_accounts_numbers
(
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    free_number  bigint not null,
    account_type_id bigint not null,
    constraint account_type_fk foreign key (account_type_id) references account_type (id) ON DELETE CASCADE
);
