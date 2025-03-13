package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthPaymentRepository extends JpaRepository<AuthPayment, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AuthPayment a WHERE a.id = :authPaymentId")
    Optional<AuthPayment> findByIdForUpdate(@Param("authPaymentId") UUID authPaymentId);

    default AuthPayment findByIdForUpdateOrThrow(UUID authPaymentId) {
        return findByIdForUpdate(authPaymentId).orElseThrow(() ->
                new EntityNotFoundException("AuthPayment not found for id: " + authPaymentId));
    }
}
