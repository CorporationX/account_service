package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthPaymentRepository extends JpaRepository<AuthPayment, UUID> {
    boolean existsById(UUID id);

    default AuthPayment findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Not found AuthPayment with id " + id));
    }

}
