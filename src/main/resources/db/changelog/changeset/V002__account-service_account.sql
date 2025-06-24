INSERT INTO accounts (number, owner_type, owner_id, account_type, currency, status, created_at, updated_at, balance, description)
VALUES
    ('123123123', 'USER', '1', 'INDIVIDUAL', 'EUR', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '123.123', 'test description 123'),
    ('234234234', 'PROJECT', '11', 'BUSINESS', 'USD', 'FROZEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '234.234', 'test description 234'),
    ('345345345', 'USER', '2', 'CURRENCY', 'RUB', 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '345.345', 'test description 345'),
    ('456456456', 'PROJECT', '12', 'SAVINGS', 'EUR', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '456.456', 'test description 456'),
    ('567567567', 'USER', '3', 'CREDIT', 'USD', 'FROZEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '567.567', 'test description 567'),
    ('678678678', 'PROJECT', '13', 'CURRENCY', 'RUB', 'CLOSED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '678.678', 'test description 678');