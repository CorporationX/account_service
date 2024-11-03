package faang.school.accountservice.repository;

import faang.school.accountservice.entity.OperationTypeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationTypeMappingRepository extends JpaRepository<OperationTypeMapping, Long> {
}
