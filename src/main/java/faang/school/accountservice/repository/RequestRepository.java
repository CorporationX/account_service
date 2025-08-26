package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
