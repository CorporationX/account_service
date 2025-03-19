CREATE TABLE users
(
    id       BIGINT             NOT NULL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL
);

INSERT INTO users (id, username)
VALUES (1, 'FirstName'),
       (2, 'SecondName'),
       (3, 'ThirdName'),
       (100, 'AccountTestName'),
       (200, 'AccountSecondTestName');