package faang.school.accountservice.repository;

import faang.school.accountservice.model.balance.BalanceAuthPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceAuthPaymentRepository extends JpaRepository<BalanceAuthPayment, UUID> {
}
