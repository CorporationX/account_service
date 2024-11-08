package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import faang.school.accountservice.enums.RequestStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends CrudRepository<Request, UUID> {
    List<Request> findAllByStatusAndScheduledAtBefore(RequestStatus status, LocalDateTime scheduledAt);
}
