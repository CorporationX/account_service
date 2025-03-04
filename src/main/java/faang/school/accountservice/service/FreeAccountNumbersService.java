package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public void addFreeAccountNumber(AccountType accountType, String accountNumber) {
        FreeAccountNumber freeAccountNumber = FreeAccountNumber.builder()
                .id(new FreeAccountNumberId(accountType, accountNumber))
                .build();
        freeAccountNumbersRepository.save(freeAccountNumber);
    }

    @Transactional
    public AccountNumberSequence createCounterForAccountType(AccountType accountType) {
        return accountNumbersSequenceRepository.getByAccountType(accountType.name())
                .orElseGet(() -> accountNumbersSequenceRepository
                        .save(AccountNumberSequence.builder()
                                .accountType(accountType)
                                .currentCounter(0L)
                                .updateAt(LocalDateTime.now())
                                .build())
                );
    }

    @Transactional
    public boolean incrementCounterIfMatches(AccountType accountType, long expectedValue) {
        Optional<Long> newCounterValue = accountNumbersSequenceRepository
                .incrementCounter(accountType.name(), expectedValue);
        return newCounterValue.isPresent();
    }

    @Transactional
    public void executeWithNewAccountNumber(AccountType accountType, Consumer<String> executeLambda) {
        Optional<String> freeAccountNumber = freeAccountNumbersRepository
                .getAndDeleteFirstFreeAccountNumber(accountType.name())
                .map(FreeAccountNumberId::getAccountNumber);

        freeAccountNumber.ifPresentOrElse(executeLambda,
                () -> {
                    String newAccountNumber = incrementAndGet(accountType);
                    executeLambda.accept(newAccountNumber);
                });
    }

    @Transactional
    private String incrementAndGet(AccountType accountType) {
        AccountNumberSequence accountNumber = accountNumbersSequenceRepository
                .getByAccountType(accountType.name())
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Счетчик для типа счета: %s не найден", accountType.name())));
        accountNumbersSequenceRepository
                .incrementCounter(accountType.name(), accountNumber.getCurrentCounter());
        FreeAccountNumberId freeAccountNumberId = freeAccountNumbersRepository
                .getAndDeleteFirstFreeAccountNumber(accountType.name())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Свободный номер для типа счета: %s не найден", accountType.name())));

        return freeAccountNumberId.getAccountNumber();
    }
}
