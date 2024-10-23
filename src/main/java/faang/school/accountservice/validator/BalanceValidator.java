package faang.school.accountservice.validator;

import faang.school.accountservice.model.dto.BalanceDto;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceValidator {
    private final BalanceRepository balanceRepository;

    public void balanceExists(BalanceDto balanceDto) {
        if (!balanceRepository.existsById(balanceDto.id()))
            throw new EntityNotFoundException("Balance with ID %d not found".formatted(balanceDto.id()));
    }
}
