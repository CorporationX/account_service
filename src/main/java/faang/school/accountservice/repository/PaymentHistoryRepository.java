package faang.school.accountservice.repository;

import faang.school.accountservice.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}