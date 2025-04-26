package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface RequestEventRepository extends JpaRepository<RequestEvent, UUID> {
    List<RequestEvent> findAllByOrderByCreatedAtAsc();
}
