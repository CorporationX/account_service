package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
}