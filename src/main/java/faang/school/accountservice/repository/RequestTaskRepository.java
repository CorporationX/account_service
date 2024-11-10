package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {
    List<RequestTask> findByRequestId(UUID requestId);
}

