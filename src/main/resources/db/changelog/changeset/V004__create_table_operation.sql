CREATE UNIQUE INDEX uq_active_operation_per_user
ON operation(user_id)
WHERE active = true;