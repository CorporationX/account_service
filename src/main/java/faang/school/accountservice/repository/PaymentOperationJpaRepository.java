package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentOperation;
import faang.school.accountservice.enums.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentOperationJpaRepository extends JpaRepository<PaymentOperation, UUID> {
    @Query("""
            SELECT COUNT(po) > 0 FROM PaymentOperation po
            WHERE   po.id = :id AND po.status <> :status
            """)
    boolean existsByIdAndStatusIsNot(UUID id, OperationStatus status);
}
