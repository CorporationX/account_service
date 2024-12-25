package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {

    List<RequestTask> findRequestTaskByRequestId(Long requestId);
}
