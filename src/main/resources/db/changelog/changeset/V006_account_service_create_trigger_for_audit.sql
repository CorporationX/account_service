CREATE OR REPLACE FUNCTION update_balance_audit RETURNS TRIGGER AS
$$
BEGIN
INSERT INTO balance_audit (
        account_id,
        account_number,
        balance_version,
        authorized_balance,
        actual_balance,
        operation_id,
        created_at
    )
    VALUES (
            NEW.account_id,
            (SELECT account_number FROM account WHERE id = NEW.account_id),
            NEW.balance_version,
            NEW.authorized_balance,
            NEW.actual_balance,
            NEW.operation_id,
            CURRENT_TIMESTAMP
        );
 RETURN NEW;
 END;
 $$ LANGUAGE plpgsql;

 CREATE TRIGGER trigger_update_audit
 AFTER UPDATE
 ON balance
 FOR EACH ROW
 EXECUTE PROCEDURE update_balance_audit();