alter sequence account_id_seq restart with 1;
insert into account(account_number, owner_id, owner_type, account_type, currency, account_status, version)
values ('12312443224567', 1, 0, 0, 0, 0, 1),
       ('123124432345368', 2, 1, 1, 1, 0, 1),
       ('1231244324235785', 5, 0, 0, 2, 0, 1);