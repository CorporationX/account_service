INSERT INTO owner("name")
VALUES ('TEST');

INSERT INTO type("name")
VALUES ('settlement'),
       ('subsettlement'),
       ('letter of credit'),
       ('budget'),
       ('credit'),
       ('deposit');

INSERT INTO account("number", "owner_id", "type_id", "currency", "status", "version")
VALUES ('test_account', 1, 1, 'USD', 'ACTIVE', 0);