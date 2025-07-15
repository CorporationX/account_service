CREATE TABLE IF NOT EXIST balance_audit (
    id bigserial PRIMARY KEY,
    balance_id BIGINT NOT NULL,
    account_number varchar(20) NOT NULL
            CHECK (LENGTH(number) >= 12 AND LENGTH(number) <= 20),
    version INTEGER NOT NULL,
    authorization_balance DECIMAL(19, 4) NOT NULL,
    actual_balance DECIMAL(19, 4) NOT NULL,
    operation_id BIGINT NOT NULL,
    created_at timestamptz NOT NULL DEFAULT current_timestamp

    CONSTRAINT fk_balance_id FOREIGN KEY (balance_id) REFERENCES balance (id)
)

CREATE FUNCTION prevent_update() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'YOU CAN NOT UPDATE BALANCE AUDIT TABLE!';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER no_update_trigger
BEFORE UPDATE ON balance_audit
FOR EACH ROW
EXECUTE FUNCTION prevent_update();

CREATE INDEX idx_balance_audit_account_id ON balance_audit (account_id);
CREATE INDEX idx_balance_audit_balance_id ON balance_audit (balance_id);