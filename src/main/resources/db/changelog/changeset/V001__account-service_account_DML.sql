insert into owner("name")
values ('TEST');

insert into type("name")
values ('settlement'),
       ('subsettlement'),
       ('letter of credit'),
       ('budget'),
       ('credit'),
       ('deposit');
--
--insert into account(
--number,owner_id, type_id, balance_id, currency_id, status, created_at,updated_at,closed_at,  version)
--values ('test_account', 1, 1, 1, 'RUB', 'ACTIVE', 0);