package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FreeAccountNumbersService {
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumbersRepository freeAccountNumbersRepository;

    @Transactional
    public AccountNumberSequence createCounterForAccountType(String accountType) {
        return accountNumbersSequenceRepository.getByAccountType(accountType)
                .orElseGet(() -> accountNumbersSequenceRepository
                        .save(AccountNumberSequence.builder()
                                .accountType(accountType)
                                .currentCounter(0L)
                                .updateAt(LocalDateTime.now())
                                .build())
                );
    }

    @Transactional
    public boolean incrementCounterIfMatches(String accountType, long expectedValue) {
        Optional<Long> newCounterValue = accountNumbersSequenceRepository.incrementCounter(accountType, expectedValue);
        return newCounterValue.isPresent();
    }
}
