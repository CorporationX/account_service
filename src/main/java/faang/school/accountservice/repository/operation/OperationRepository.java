package faang.school.accountservice.repository.operation;

import faang.school.accountservice.entity.PendingOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<PendingOperation, Long> {

    Optional<PendingOperation> findByOperationKey(String key);
}
