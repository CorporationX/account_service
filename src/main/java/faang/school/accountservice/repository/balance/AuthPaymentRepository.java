package faang.school.accountservice.repository.balance;

import faang.school.accountservice.model.balance.AuthPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthPaymentRepository extends JpaRepository<AuthPayment, UUID> {
}
