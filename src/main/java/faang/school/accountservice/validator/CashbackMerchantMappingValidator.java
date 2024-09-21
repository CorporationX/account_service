package faang.school.accountservice.validator;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.CashbackMerchantMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CashbackMerchantMappingValidator {
    private final CashbackMerchantMappingRepository cashbackMerchantMappingRepository;

    public void validateExists(Long cashbackMerchantMappingId) {
        if (!cashbackMerchantMappingRepository.existsById(cashbackMerchantMappingId)) {
            throw new EntityNotFoundException("CashbackMerchantMapping with id: %d not found."
                .formatted(cashbackMerchantMappingId));
        }
    }
}