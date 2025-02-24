-- Write your sql migration here!
CREATE TABLE account (
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    number     varchar(20) UNIQUE NOT NULL CHECK( LENGTH(number) BETWEEN 12 AND  20 ),
    project_id bigint,
    user_id    bigint,
    type       smallint    NOT NULL,
    currency   smallint    NOT NULL,
    status     smallint    NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz DEFAULT CURRENT_TIMESTAMP,
    closed_at  timestamptz DEFAULT CURRENT_TIMESTAMP,
    version    smallint    NOT NULL DEFAULT 0
);

--Платежный счет имеет следующие параметры:
--номер - строка от 12 до 20 цифр
--владельца - учесть, что владельцем может быть как пользователь, таки. проект
--тип - можно придумать несколько, например, расчетный счет для физ./юр. лиц, валютный счёт, и т.д
--валюта - RUB, EUR, USD, …
--статус - аккаунт может быть действующим, замороженным, закрытым
--время создания
--время изменения
--время закрытия
--версия счёта

