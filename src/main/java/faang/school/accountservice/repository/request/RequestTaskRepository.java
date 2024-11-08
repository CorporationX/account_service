package faang.school.accountservice.repository.request;

import faang.school.accountservice.entity.request.RequestTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTaskRepository extends JpaRepository<RequestTask, String> {

}

