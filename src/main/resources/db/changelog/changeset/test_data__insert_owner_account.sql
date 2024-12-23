CREATE TABLE IF NOT EXISTS owner
(
    id         BIGSERIAL PRIMARY KEY,
    type       VARCHAR(32) NOT NULL,
    owner_id   BIGINT      NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz
    );

CREATE INDEX idx_owner_id_type ON owner (owner_id, type);

CREATE TABLE IF NOT EXISTS account
(
    id             BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL UNIQUE CHECK ( LENGTH(account_number) >= 12
    AND account_number SIMILAR TO '[0-9]+'),
    owner_id       BIGINT      NOT NULL,
    type           VARCHAR(64) NOT NULL,
    currency       VARCHAR(32) NOT NULL,
    status         VARCHAR(32) NOT NULL,
    created_at     TIMESTAMPTZ          DEFAULT current_timestamp,
    updated_at     TIMESTAMPTZ,
    closed_at      TIMESTAMPTZ,
    version        BIGINT      NOT NULL DEFAULT 0,
    is_verified    BOOLEAN     NOT NULL,
    notes          VARCHAR(4096),

    CONSTRAINT fk_account_owner FOREIGN KEY (owner_id) REFERENCES owner (id)
    );

CREATE INDEX idx_owner_id ON account (owner_id);
CREATE INDEX idx_account_number ON account (account_number);

DO $$
BEGIN
FOR i IN 1..10 LOOP
                INSERT INTO owner (
                    type,
                    owner_id,
                    created_at,
                    updated_at
                )
                VALUES (
                           CASE WHEN i % 2 = 0 THEN 'USER' ELSE 'PROJECT' END, -- Чередуем типы владельцев
                           1000 + i, -- Уникальный owner_id
                           current_timestamp - interval '6 months' + (i * interval '3 days'), -- Дата создания
                           current_timestamp -- Дата обновления
                       );
END LOOP;
END $$;

DO $$
BEGIN
FOR i IN 1..50 LOOP
                INSERT INTO account (
                    account_number,
                    owner_id,
                    type,
                    currency,
                    status,
                    created_at,
                    updated_at,
                    closed_at,
                    version,
                    is_verified,
                    notes
                )
                VALUES (
                           LPAD(i::text, 12, '0'), -- Генерация номера счёта длиной 12 цифр
                           (SELECT id FROM owner OFFSET floor(random() * (SELECT count(*) FROM owner)) LIMIT 1), -- Случайный owner_id
                           CASE
                               WHEN i % 8 = 0 THEN 'CURRENT'
                               WHEN i % 8 = 1 THEN 'SAVINGS'
                               WHEN i % 8 = 2 THEN 'FOREIGN_EXCHANGE'
                               WHEN i % 8 = 3 THEN 'CREDIT'
                               WHEN i % 8 = 4 THEN 'JOINT'
                               WHEN i % 8 = 5 THEN 'BUSINESS'
                               WHEN i % 8 = 6 THEN 'ESCROW'
                               ELSE 'DEPOSIT'
                               END, -- Тип счёта
                           CASE
                               WHEN i % 3 = 0 THEN 'USD'
                               WHEN i % 3 = 1 THEN 'EUR'
                               ELSE 'RUB'
                               END, -- Валюта
                           CASE
                               WHEN i % 6 = 0 THEN 'ACTIVE'
                               WHEN i % 6 = 1 THEN 'FROZEN'
                               WHEN i % 6 = 2 THEN 'CLOSED'
                               WHEN i % 6 = 3 THEN 'PENDING'
                               WHEN i % 6 = 4 THEN 'BLOCKED'
                               ELSE 'SUSPENDED'
                               END, -- Статус
                           current_timestamp - interval '6 months' + (i * interval '2 days'), -- Дата создания
                           current_timestamp - interval '6 months' + (i * interval '1 day'), -- Дата обновления
                           CASE WHEN i % 6 = 2 THEN current_timestamp ELSE NULL END, -- Дата закрытия (только для CLOSED)
                           0, -- Версия
                           (i % 2 = 0), -- is_verified: true для чётных, false для нечётных
                           'Test account ' || i -- Примечания
                       );
END LOOP;
END $$;