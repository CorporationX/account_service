package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.cashback.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTypeCashbackRepository extends JpaRepository<OperationType, Long> {
}
