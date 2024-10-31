package faang.school.accountservice.repository;

import faang.school.accountservice.model.balance.BalanceAuthPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BalanceAuthPaymentRepository extends JpaRepository<BalanceAuthPayment, UUID> {
}
