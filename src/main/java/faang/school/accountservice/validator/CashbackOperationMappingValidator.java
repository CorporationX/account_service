package faang.school.accountservice.validator;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.CashbackOperationMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CashbackOperationMappingValidator {
    private final CashbackOperationMappingRepository cashbackOperationMappingRepository;

    public void validateExists(Long cashbackOperationMappingId) {
        if (!cashbackOperationMappingRepository.existsById(cashbackOperationMappingId)) {
            throw new EntityNotFoundException("CashbackOperationMapping with id: %d not found."
                .formatted(cashbackOperationMappingId));
        }
    }
}
