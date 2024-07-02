package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentOperation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentOperationRepository {
    private final PaymentOperationJpaRepository repository;

    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    public PaymentOperation save(PaymentOperation paymentOperation) {
        return repository.save(paymentOperation);
    }

    public PaymentOperation findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> {
                    String message = String.format("Operation with number %s doesn't exist in system.", id);
                    throw new EntityNotFoundException(message);
                });
    }
}
