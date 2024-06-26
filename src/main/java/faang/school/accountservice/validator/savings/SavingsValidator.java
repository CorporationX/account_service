package faang.school.accountservice.validator.savings;

import faang.school.accountservice.dto.savings.SavingsAccountToCreateDto;
import faang.school.accountservice.exception.NotFoundException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SavingsValidator {
    private final TariffRepository tariffRepository;
    private final AccountRepository accountRepository;
    public void validateCreate(SavingsAccountToCreateDto dto) {
    }
}
