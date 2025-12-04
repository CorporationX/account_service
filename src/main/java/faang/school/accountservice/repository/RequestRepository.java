package faang.school.accountservice.repository;

import faang.school.accountservice.model.Request;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByNotificationPendingTrue();
}
