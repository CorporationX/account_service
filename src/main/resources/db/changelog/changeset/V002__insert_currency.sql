INSERT INTO currency (id, create_at, updated_at, name, iso_code, symbol)
VALUES
    (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Российский рубль', 'RUB', '₽'),
    (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Доллар США', 'USD', '$'),
    (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Евро', 'EUR', '€');