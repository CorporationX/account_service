package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.jpa.AccountNumbersSequenceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class AccountNumbersSequenceRepository {
    private final AccountNumbersSequenceJpaRepository repository;

    public void createAccountNumbersSequence(AccountType type) {
        var accountNumbersSequence = AccountNumbersSequence.builder()
                .sequence(0L)
                .type(type)
                .code(String.valueOf(type.getCode()))
                .build();

        var savedNumber = repository.save(accountNumbersSequence);
        System.out.println(savedNumber);
    }

    public boolean incrementAccountNumbersSequence(AccountType type) {
        try {
            AccountNumbersSequence sequence = getByType(type);

            sequence.incrementSequence();
            repository.save(sequence);
        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    public AccountNumbersSequence getByType(AccountType type) {
        return repository.findByType(type.name())
                .orElseThrow(() -> {
                    String message = String.format("There is no number sequence for type %s", type.name());
                    return new NoSuchElementException(message);
                });
    }
}
