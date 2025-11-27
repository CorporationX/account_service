package faang.school.accountservice.repository;

import faang.school.accountservice.entity.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID>, JpaSpecificationExecutor<Request> {

    boolean existsByLockValueAndIsOpenTrue(String lockValue);

    Optional<Request> findByIdempotencyToken(UUID idempotencyToken);

    List<Request> findAllByIsOpen(Boolean isOpen);
}