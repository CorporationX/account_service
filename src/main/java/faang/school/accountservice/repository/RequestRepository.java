package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends CrudRepository<Request, Long> {

    Optional<Request> findByIdempotencyToken(UUID idempotencyToken);

    @Query("SELECT r FROM Request r WHERE r.userId = :userId AND r.isOpen = true")
    List<Request> findByUserIdAndIsOpen(@Param("userId") Long userId);

    @Query("SELECT r FROM Request r WHERE r.updatedAt >= :timestamp")
    List<Request> findRecentlyUpdated(@Param("timestamp") Instant timestamp);

    @Query("SELECT r FROM Request r WHERE r.lockValue = :lockValue AND r.isOpen = true")
    List<Request> findByLockValueAndIsOpen(@Param("lockValue") Long lockValue);
}
