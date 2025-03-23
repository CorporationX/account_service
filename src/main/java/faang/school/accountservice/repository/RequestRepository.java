package faang.school.accountservice.repository;

import faang.school.accountservice.enums.RequestStatus;
import faang.school.accountservice.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

    List<Request> findByRequestStatus(RequestStatus status);
}
