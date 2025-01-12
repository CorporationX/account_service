package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequestTaskRepository extends JpaRepository<RequestTask, UUID> {
    List<RequestTask> findAllByRequestId(UUID requestId);
}
