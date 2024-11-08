package faang.school.accountservice.service.impl;


import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import faang.school.accountservice.service.NumberGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NumberGeneratorImpl implements NumberGenerator {

    private final FreeAccountNumbersRepository freeAccountNumbersRepository;;

    @Override
    public String prepareNumberForAccount() {
        return freeAccountNumbersRepository.getFreeAccountNumberByType(AccountType.SAVINGS_ACCOUNT.name()).orElseThrow(
                () -> new EntityNotFoundException("Number not found"));
    }
}
