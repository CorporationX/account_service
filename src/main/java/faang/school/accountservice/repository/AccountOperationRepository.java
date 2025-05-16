package faang.school.accountservice.repository;

import faang.school.accountservice.dto.OperationStatus;
import faang.school.accountservice.dto.OperationType;
import faang.school.accountservice.model.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, UUID> {

    boolean existsByPaymentOperationId(UUID paymentOperationId);

    @Query("""
            SELECT ac FROM  AccountOperation ac
            WHERE ac.operationType = :type
            AND ac.operationStatus = :status
            AND ac.paymentOperationId = :id
            """)
    Optional<AccountOperation> findAuthOperation(UUID id, OperationType type, OperationStatus status);

    boolean existsByPaymentOperationIdAndOperationType(UUID paymentOperationId, OperationType type);
}
