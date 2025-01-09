package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    List<Operation> findByStatusInAndCreatedAtAfter(List<String> statuses, LocalDateTime since);

    @Query("SELECT o FROM Operation o WHERE o.accountId = :accountId AND o.createdAt > :since " +
            "AND o.cashbackProcessed = false")
    List<Operation> findUnprocessedOperationsByAccountAndDate(
            @Param("accountId") Long accountId,
            @Param("since") LocalDateTime since
    );
}