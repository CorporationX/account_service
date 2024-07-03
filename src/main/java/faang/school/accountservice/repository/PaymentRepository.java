package faang.school.accountservice.repository;

import faang.school.accountservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query(value = "SELECT * FROM payment WHERE scheduled_at <= NOW() AND payment_status = 'READY_TO_CLEAR' ORDER BY scheduled_at LIMIT 10", nativeQuery = true)
    List<Payment> findReadyToClearTransactions();
}