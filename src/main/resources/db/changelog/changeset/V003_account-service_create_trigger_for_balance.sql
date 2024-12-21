CREATE OR REPLACE FUNCTION update_balance() RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trigger_update_balance_at ON balance;

CREATE TRIGGER trigger_update_balance_at
    BEFORE UPDATE
    ON balance
    FOR EACH ROW
EXECUTE PROCEDURE update_balance();