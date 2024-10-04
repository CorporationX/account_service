INSERT INTO account (id, number, type, currency, status, created_at, updated_at, closed_at, version)
VALUES (1, '8800 0000 0000 0008', 'SAVINGSACCOUNT', 'USD', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 1);

INSERT INTO savings_account (id, account_id, last_interest_calculation_date, created_at, updated_at, version)
VALUES (1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

INSERT INTO tariff (id, type, savings_account_id, applied_at)
VALUES (1, 'PREMIUM', 1, CURRENT_TIMESTAMP);

INSERT INTO rate_history (id, tariff_id, rate, created_at)
VALUES (1, 1, 0.08, CURRENT_TIMESTAMP);