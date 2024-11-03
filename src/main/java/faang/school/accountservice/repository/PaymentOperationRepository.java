package faang.school.accountservice.repository;

import faang.school.accountservice.model.payment_operation.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentOperationRepository extends JpaRepository<PaymentOperation, UUID> {
}
