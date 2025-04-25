package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface RequestEventRepository extends JpaRepository<RequestEvent, UUID> {
    List<RequestEvent> findAllByOrderByCreatedAtAsc();
}
