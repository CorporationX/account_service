package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.Request;
import faang.school.accountservice.model.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByIdempotencyToken(UUID idempotencyToken);

    boolean existsByAccountIdAndStatus(Long accountId, RequestStatus status);
}
