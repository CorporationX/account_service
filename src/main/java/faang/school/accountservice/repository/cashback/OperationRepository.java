package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findOperationByIdAndCreatedAtAfter(long accountId, LocalDateTime from);
}