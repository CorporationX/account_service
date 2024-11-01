package faang.school.accountservice.service;

import faang.school.accountservice.dto.FreeAccountNumberDto;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exception.NoAvailableAccountNumberException;
import faang.school.accountservice.mapper.FreeAccountNumberMapper;
import faang.school.accountservice.model.FreeAccountNumber;
import faang.school.accountservice.model.FreeAccountNumberId;
import faang.school.accountservice.repository.AccountNumbersSequenceRepository;
import faang.school.accountservice.repository.FreeAccountNumbersRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
@RequiredArgsConstructor
public class FreeAccountNumbersServiceImpl implements FreeAccountNumbersService {

    private final FreeAccountNumbersRepository accountNumbersRepository;
    private final AccountNumbersSequenceRepository accountNumbersSequenceRepository;
    private final FreeAccountNumberMapper mapper;

    @Value("${spring.account.pattern-number}")
    private String patternNumber;

    @Override
    @Transactional
    public void retrieveFreeAccountNumber(AccountType type, Consumer<FreeAccountNumber> numberConsumer) {
        log.info("Attempting to find a free account number for type: {}", type);
        FreeAccountNumber freeAccountNumber = accountNumbersRepository
                .findAndRemoveFirstFreeAccountNumber(type.name());
        if (freeAccountNumber == null) {
            log.warn("No free account number found for type: {}", type);
            throw new NoAvailableAccountNumberException("No free account number available for account type: " + type);
        }
        numberConsumer.accept(freeAccountNumber);
    }

    @Override
    @Transactional
    @Cacheable(value = "accountNumberCache")
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retryable.max-attempts}",
            backoff = @Backoff(delayExpression = "${retryable.delay}"))
    public FreeAccountNumberDto generateAccountNumber(AccountType type) {
        accountNumbersSequenceRepository.increment(type.name());
        FreeAccountNumberId number = FreeAccountNumberId.builder()
                .accountType(type)
                .number(createValidNumber(type))
                .build();
        FreeAccountNumber accountNumber = FreeAccountNumber.builder()
                .id(number)
                .build();
        accountNumbersRepository.save(accountNumber);
        log.info("Generated new account number for type: {}", type);
        return mapper.toAccountNumberDto(accountNumber);
    }

    private String createValidNumber(AccountType type) {
        long currentCounter = accountNumbersSequenceRepository.getCounterByType(type.name());
        String formattedCode = type.getCode() + patternNumber;
        return String.valueOf(Long.parseLong(formattedCode) + currentCounter);
    }
}