CREATE TABLE users
(
    id       BIGINT             NOT NULL PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    email    VARCHAR(64) UNIQUE NOT NULL
);

INSERT INTO users (id, username, email)
VALUES (1, 'FirstName', 'ex1@mail.com'),
       (2, 'SecondName', 'ex2@mail.com'),
       (3, 'ThirdName', 'ex3@mail.com'),
       (100, 'AccountTestName', 'ex100@mail.com'),
       (200, 'AccountSecondTestName', 'ex200@mail.com');