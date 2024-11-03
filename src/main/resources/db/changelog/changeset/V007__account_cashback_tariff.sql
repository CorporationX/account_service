CREATE TABLE cashback_tariff
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(256)             NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE operation_type_mapping
(
    id             BIGSERIAL PRIMARY KEY,
    operation_type SMALLINT      NOT NULL,
    percentage     DECIMAL(5, 2) NOT NULL
);

CREATE TABLE merchant_mapping
(
    id          BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT        NOT NULL,
    percentage  DECIMAL(5, 2) NOT NULL
);

CREATE TABLE cashback_tariff_operation_type_mapping
(
    id                        BIGSERIAL PRIMARY KEY,
    cashback_tariff_id        BIGINT NOT NULL,
    operation_type_mapping_id BIGINT NOT NULL,

    CONSTRAINT fx_cashback_tariff_id FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff (id),
    CONSTRAINT fx_operation_type_mapping_id FOREIGN KEY (operation_type_mapping_id) REFERENCES operation_type_mapping (id)
);


CREATE TABLE cashback_tariff_merchant_mapping
(
    id                  BIGSERIAL PRIMARY KEY,
    cashback_tariff_id  BIGINT NOT NULL,
    merchant_mapping_id BIGINT NOT NULL,

    CONSTRAINT fx_cashback_tariff_id FOREIGN KEY (cashback_tariff_id) REFERENCES cashback_tariff (id),
    CONSTRAINT fx_merchant_mapping_id FOREIGN KEY (merchant_mapping_id) REFERENCES merchant_mapping (id)
);

ALTER TABLE account
    ADD COLUMN IF NOT EXISTS cashback_tariff_id BIGINT REFERENCES cashback_tariff (id),
    DROP COLUMN IF EXISTS balance_id;

ALTER TABLE balance
    ALTER COLUMN account_id SET NOT NULL;

CREATE UNIQUE INDEX ON operation_type_mapping (operation_type, percentage);
CREATE UNIQUE INDEX ON merchant_mapping (merchant_id, percentage);

ALTER TABLE balance_audit
    ADD COLUMN IF NOT EXISTS request_by uuid NOT NULL REFERENCES request (id);