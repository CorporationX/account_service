CREATE TABLE tariff
(
    id           BIGSERIAL PRIMARY KEY,
    type         VARCHAR(128) NOT NULL,
    rate_history JSON         NOT NULL
);