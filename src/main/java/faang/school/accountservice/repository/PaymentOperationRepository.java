package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentOperation;
import faang.school.accountservice.enums.OperationStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentOperationRepository {
    private final PaymentOperationJpaRepository repository;

    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

    public PaymentOperation save(PaymentOperation paymentOperation) {
        return repository.save(paymentOperation);
    }

    public PaymentOperation findById(UUID id) {
        return repository
                .findById(id)
                .orElseThrow(() -> {
                    String message = String.format("Operation with number %s doesn't exist in system.", id);
                    throw new EntityNotFoundException(message);
                });
    }

    public boolean existsAndIsntRefused(UUID paymentId) {
        return repository.existsByIdAndStatusIsNot(paymentId, OperationStatus.REFUSED);
    }
}
