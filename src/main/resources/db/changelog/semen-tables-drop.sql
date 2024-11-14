drop INDEX idx_auth_payment_status_id_created_at;
ALTER TABLE account drop CONSTRAINT fk_account_cashback_tariff;
ALTER TABLE account drop COLUMN cashback_tariff_id;
drop INDEX idx_cashback_merchant_cashback_tariff_id_merchant_id;
drop TABLE cashback_merchant;
drop TABLE cashback_operation_type ;
drop INDEX uniq_merchant_user_id;
drop INDEX uniq_merchant_project_id;
drop TABLE merchant;
drop INDEX uniq_cashback_tariff_name;
drop TABLE cashback_tariff;
--

drop table tariff_to_savings_account_binding;
drop table savings_account;
drop table tariff_rate;
drop table tariff;