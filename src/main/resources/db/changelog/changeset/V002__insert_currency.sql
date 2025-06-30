INSERT INTO currency (id, created_at, updated_at, version, name, iso_code, symbol)
VALUES
    (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Российский рубль', 'RUB', '₽'),
    (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Доллар США', 'USD', '$'),
    (uuid_generate_v4(), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0, 'Евро', 'EUR', '€');