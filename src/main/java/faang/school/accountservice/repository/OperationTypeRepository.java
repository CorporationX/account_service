package faang.school.accountservice.repository;

import faang.school.accountservice.entity.type.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
}
