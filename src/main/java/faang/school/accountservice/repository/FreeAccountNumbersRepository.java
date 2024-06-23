package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.jpa.FreeAccountNumbersJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FreeAccountNumbersRepository {
    public static final String FORMAT = "%d%016d";
    private final FreeAccountNumbersJpaRepository repository;

    public void createNewFreeNumber(AccountNumbersSequence accountNumbersSequence) {
        var number = String.format(FORMAT,
                accountNumbersSequence.getType().getCode(),
                accountNumbersSequence.getSequence());

        var freeAccountNumber = FreeAccountNumber.builder()
                .number(number)
                .type(accountNumbersSequence.getType())
                .build();

        repository.save(freeAccountNumber);
    }

    public Optional<FreeAccountNumber> getFreeAccountNumber(AccountType type) {
        Optional<FreeAccountNumber> accountNumber = repository.getReferenceByType(type.name());

        accountNumber.ifPresent(freeAccountNumber -> repository.deleteById(freeAccountNumber.getId()));

        return accountNumber;
    }
}
