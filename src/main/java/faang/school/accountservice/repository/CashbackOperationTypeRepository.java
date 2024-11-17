package faang.school.accountservice.repository;

import faang.school.accountservice.entity.cacheback.CashbackOperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CashbackOperationTypeRepository extends JpaRepository<CashbackOperationType, UUID> {
}