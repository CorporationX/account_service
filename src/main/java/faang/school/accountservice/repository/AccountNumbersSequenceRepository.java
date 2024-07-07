package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.repository.jpa.AccountNumbersSequenceJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountNumbersSequenceRepository {
    private final AccountNumbersSequenceJpaRepository repository;

    public AccountNumbersSequence createAccountNumbersSequence(AccountType type) {
        var accountNumbersSequence = AccountNumbersSequence.builder()
                .sequence(0L)
                .type(type)
                .code(String.valueOf(type.getCode()))
                .build();

        return repository.save(accountNumbersSequence);
    }

    public boolean incrementAccountNumbersSequence(AccountNumbersSequence sequence) {
        try {
            sequence.incrementSequence();
            repository.save(sequence);
        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    public AccountNumbersSequence findOrCreateByType(AccountType type) {
        return repository.findByType(type.name()).orElseGet(() -> createAccountNumbersSequence(type));
    }
}
