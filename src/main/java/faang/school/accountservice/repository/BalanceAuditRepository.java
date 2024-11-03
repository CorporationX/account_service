package faang.school.accountservice.repository;

import faang.school.accountservice.dto.CashbackBalanceDto;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.enums.OperationType;
import faang.school.accountservice.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BalanceAuditRepository extends JpaRepository<BalanceAudit, Long> {

    @Query("""
            SELECT new faang.school.accountservice.dto.CashbackBalanceDto(ba.balance.id, AVG(ba.curFactBalance))
            FROM BalanceAudit ba JOIN ba.request r
            WHERE ba.createdAt >= :afterDateTime
                AND r.type IN (:operationTypes)
                AND r.status = :status
            GROUP BY ba.balance.id
            """)
    List<CashbackBalanceDto> findFactAverageBalance(LocalDateTime afterDateTime,
                                                    List<OperationType> operationTypes,
                                                    RequestStatus status);
}
