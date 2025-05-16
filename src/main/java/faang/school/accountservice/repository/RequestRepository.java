package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.request.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
    Page<Request> findAllByStatusAndScheduledAtBefore(RequestStatus status, LocalDateTime time, Pageable pageable);
}
