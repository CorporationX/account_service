package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentOperationJpaRepository extends JpaRepository<PaymentOperation, Long> {
}