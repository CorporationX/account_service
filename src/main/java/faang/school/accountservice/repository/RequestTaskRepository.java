package faang.school.accountservice.repository;

import faang.school.accountservice.entity.RequestTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTaskRepository extends JpaRepository<RequestTask, Long> {
}
