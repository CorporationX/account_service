CREATE OR REPLACE FUNCTION update_balance() RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at := CURRENT_TIMESTAMP;
    NEW.balance_version := OLD.balance_version + 1;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_balance_at
    BEFORE UPDATE
    ON balance
    FOR EACH ROW
EXECUTE PROCEDURE update_balance();