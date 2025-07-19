package faang.school.accountservice.repository;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {

    Optional<Request> findByLockKeyAndIsOpenTrue(String lockKey);

    List<Request> findByRequestStatusAndIsOpenTrue(RequestStatus requestStatus);
}
