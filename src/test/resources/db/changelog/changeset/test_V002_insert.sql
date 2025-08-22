INSERT INTO users (username)
VALUES ('JohnDoe');

INSERT INTO project (name)
VALUES ('project');

INSERT INTO account (id, number, user_id, project_id, account_type, currency, status)
VALUES
('11111111-1111-1111-1111-111111111111', '12345678912345', 1, null, 0, 'RUB', 0),
('22222222-2222-2222-2222-222222222222', '23456789123456', 1, null, 1, 'RUB', 1),
('33333333-3333-3333-3333-333333333333', '34567891234567', null, 1, 2, 'RUB', 2),
('44444444-4444-4444-4444-444444444444', '45678912345678', null, 1, 3, 'RUB', 0);