package faang.school.accountservice.repository.request;

import faang.school.accountservice.entity.request.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
    @Query("""
        SELECT r.idempotencyToken FROM Request r
        WHERE r.status IN (:statuses)
    """)
    List<UUID> findAllIdsByStatusIn(@Param("statuses") List<RequestStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT r FROM Request r
        WHERE r.idempotencyToken = :id
    """)
    Optional<Request> findByIdForUpdate(@Param("id") UUID id);

    List<Request> findByStatus(RequestStatus status);
}
