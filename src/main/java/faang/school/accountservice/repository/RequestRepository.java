package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, String> {

    Optional<Request> findByLockKeyAndIsOpenTrue(String lockKey);

    Optional<Request> findByIdempotencyToken(String idempotencyToken);

}
