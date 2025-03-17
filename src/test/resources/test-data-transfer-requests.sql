INSERT INTO transfer_requests (id, sender_account_id, receiver_account_id, amount, currency, auth_payment_id,
                               payment_type, transfer_status, kafka_published, created_at, updated_at)
VALUES ('5d14fdc1-8327-4cb0-b102-f59ff9cbf5d8', 1, 2, 100.00, 'USD', '2f04fdc1-8327-4cb0-b102-f59ff9cbf5d7',
        'HOUSEHOLD', NULL, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('5d14fdc1-8327-4cb0-b102-f59ff9cbf5d4', 2, 1, 100.00, 'USD', '2f04fdc1-8327-4cb0-b102-f59ff9cbf5d7',
        'HOUSEHOLD', 'AUTHORIZED', FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);